//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.10.09 at 03:20:50 PM BST 
//


package org.linguafranca.pwdb.kdbx.jaxb.binding;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter3
    extends XmlAdapter<String, Boolean>
{


    public Boolean unmarshal(String value) {
        return (org.linguafranca.pwdb.kdbx.Helpers.toBoolean(value));
    }

    public String marshal(Boolean value) {
        return (org.linguafranca.pwdb.kdbx.Helpers.fromBoolean(value));
    }

}
