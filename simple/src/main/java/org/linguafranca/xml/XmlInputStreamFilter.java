/*
 * Copyright 2015 Jo Rabin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.linguafranca.xml;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author jo
 */
public class XmlInputStreamFilter extends InputStream {

    private final XMLEventReader xmlEventReader;
    private XMLEventWriter xmlEventWriter;

    private byte[] buffer = new byte[0];
    private volatile ByteArrayInputStream xmlInStream = new ByteArrayInputStream(buffer);
    private volatile ByteArrayOutputStream xmlWriteStream = new ByteArrayOutputStream();
    private boolean done = false;


    private InputStream inputStream;
    private XmlEventTransformer eventTransformer;

    public XmlInputStreamFilter(InputStream is, XmlEventTransformer transformer) throws XMLStreamException {
        this.inputStream = is;
        this.eventTransformer = transformer;
        XMLInputFactory inputFactory = new com.fasterxml.aalto.stax.InputFactoryImpl();
        this.xmlEventReader = inputFactory.createXMLEventReader(is);
        XMLOutputFactory outputFactory = new com.fasterxml.aalto.stax.OutputFactoryImpl();
        this.xmlEventWriter = outputFactory.createXMLEventWriter(xmlWriteStream);
    }

    /**
     * Gets bytes from the internal buffer and replenishes the buffer as necessary
     *
     * @param b      a byte array to fill
     * @param offset the offset to start from
     * @param length the number of bytes to return
     * @return the number of bytes actually returned, , -1 if end of file
     * @throws IOException on error
     */
    private int get(byte[] b, int offset, int length) throws IOException {
        if (done) {
            return -1;
        }
        int totalBytesRead = 0;
        int bytesRead;
        while ((bytesRead = xmlInStream.read(b, offset, length)) < length && !done) {
            if (bytesRead == -1) {
                try {
                    //load();
                    loadEvents();
                } catch (XMLStreamException e) {
                    throw new IOException(e);
                }
            } else {
                offset += bytesRead;
                length -= bytesRead;
                totalBytesRead += bytesRead;
            }
        }
        return bytesRead > 0 ? totalBytesRead + bytesRead : totalBytesRead;
    }


    /**
     * replenish the internal buffer
     * @throws XMLStreamException if there was a problem
     */
    private void loadEvents() throws XMLStreamException {
        if (!xmlEventReader.hasNext()) {
            done = true;
            return;
        }

        XMLEvent event = xmlEventReader.nextEvent();
        event = eventTransformer.transform(event);
        xmlWriteStream.reset();
        xmlEventWriter.add(event);
        xmlEventWriter.flush();
        xmlInStream = new ByteArrayInputStream(xmlWriteStream.toByteArray());
    }

    @Override
    public int read() throws IOException {
        byte[] buffer = new byte[1];
        if (get(buffer, 0, 1) != 1) {
            return -1;
        }
        return buffer[0] & 0xFF;
    }

    @Override
    public int read(@NotNull byte[] b, int offset, int length) throws IOException {
        return get(b, offset, length);
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

}
