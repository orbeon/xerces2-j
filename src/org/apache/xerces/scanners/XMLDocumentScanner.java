/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.scanners;

import java.io.EOFException;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.xerces.framework.XMLComponent;
import org.apache.xerces.framework.XMLComponentManager;
import org.apache.xerces.framework.XMLErrorReporter;
import org.apache.xerces.framework.XMLString;
import org.apache.xerces.framework.XMLStringBuffer;
import org.apache.xerces.readers.XMLEntityManager;
import org.apache.xerces.readers.XMLEntityScanner;
import org.apache.xerces.utils.SymbolTable;
import org.apache.xerces.utils.QName;
import org.apache.xerces.utils.XMLChar;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * This class is responsible for scanning XML document structure
 * and content. The scanner acts as the source for the document
 * information which is communicated to the document handler.
 * <p>
 * The document scanner requires the following features and 
 * properties from the component manager that uses it:
 * <ul>
 *  <li>http://xml.org/sax/features/namespaces</li>
 *  <li>http://apache.org/xml/properties/internal/symbol-table</li>
 *  <li>http://apache.org/xml/properties/internal/error-reporter</li>
 *  <li>http://apache.org/xml/properties/internal/entity-manager</li>
 * </ul>
 *
 * @author Stubs generated by DesignDoc on Mon Sep 11 11:10:57 PDT 2000
 * @author Andy Clark, IBM
 *
 * @version $Id$
 */
public class XMLDocumentScanner
    implements XMLComponent, XMLDocumentSource {

    //
    // Constants
    //

    // sax2 features

    /** Namespaces feature id. */
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";

    // xerces properties

    /** Symbol table property id. */
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";

    /** Error reporter property id. */
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";

    /** Entity manager property id. */
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";

    // debugging

    /** Debug. */
    private static final boolean DEBUG = false;

    //
    // Data
    //

    // features

    /** Namespaces. */
    protected boolean fNamespaces;

    // properties

    /** Symbol table. */
    protected SymbolTable fSymbolTable;

    /** Error reporter. */
    protected XMLErrorReporter fErrorReporter;

    /** Entity manager. */
    protected XMLEntityManager fEntityManager;

    /** DTD scanner. */
    /*** REVISIT: Add DTD support. ***
    protected XMLDTDScanner fDTDScanner;
    /***/

    // protected data

    /** Document handler. */
    protected XMLDocumentHandler fDocumentHandler;

    /** Entity scanner. */
    protected XMLEntityScanner fEntityScanner;

    /** Namespace support. */
    protected NamespaceSupport fNamespaceSupport = new NamespaceSupport();

    // private data

    /** Element QName. */
    private QName fElemQName = new QName();

    /** Attribute QName. */
    private QName fAttrQName = new QName();

    /** Element attributes. */
    private XMLAttributes fAttributes = new XMLAttributes();

    /** String. */
    private XMLString fString = new XMLString();

    /** String buffer. */
    private XMLStringBuffer fStringBuffer = new XMLStringBuffer();

    //
    // Constructors
    //

    /** Default constructor. */
    public XMLDocumentScanner() {
    } // <init>()

    //
    // Public methods
    //

    /** 
     * Scans a document. 
     * <p>
     * <strong>Note:</strong> The caller of this method is responsible
     * for having called <code>reset(XMLComponentManager)</code> before
     * any scanning and initialized the entity manager by starting the
     * document entity.
     *
     * @param complete
     *
     * @returns
     */
    public boolean scanDocument(boolean complete) 
        throws IOException, SAXException {

        // REVISIT: Currently this is implemented without the pull
        //          parser capability. In the true pull parser, the
        //          scanner initialization code would need to be
        //          moved to reset(XMLComponentManager) and the body
        //          of this method would be a state machine dispatcher
        //          within a "while (complete)" loop. -Ac

        // initialize scanner
        fEntityScanner = fEntityManager.getEntityScanner();
        fNamespaceSupport.reset();

        // scan document
        scanDocument();

        // return success
        return true;

    } // scanDocument(boolean):boolean

    //
    // Protected methods
    //

    // scanning

    /** 
     * Scans a document.
     * <p>
     * <pre>
     * [1] document ::= prolog element Misc*
     * </pre>
     */
    protected void scanDocument() throws IOException, SAXException {
        if (DEBUG) System.out.println(">>> scanDocument()");

        // call handler
        if (fDocumentHandler != null) {
            fDocumentHandler.startDocument();
        }

        // scan document
        scanProlog();
        if (fEntityScanner.scanChar() != '<') {
            // REVISIT: report error
            throw new SAXException("expected start of root element");
        }
        scanElement();
        try {
            scanMisc();
        }
        catch (EOFException e) {
            // ignore; legal end of scanning
        }

        // call handler
        if (fDocumentHandler != null) {
            fDocumentHandler.endDocument();
        }

        if (DEBUG) System.out.println("<<< scanDocument()");
    } // scanDocument()

    /** 
     * Scans the prolog.
     * <p>
     * <pre>
     * [22] prolog ::= XMLDecl? Misc* (doctypedecl Misc*)?
     * </pre> 
     */
    protected void scanProlog() throws IOException, SAXException {
        if (DEBUG) System.out.println(">>> scanProlog()");

        if (fEntityScanner.skipString("<?xml")) {
            scanXMLDecl();
        }
        scanMisc();
        if (fEntityScanner.skipString("<!DOCTYPE")) {
            scanDoctypeDecl();
        }
        scanMisc();

        if (DEBUG) System.out.println("<<< scanProlog()");
    } // scanProlog()

    /** 
     * Scans the XML declaration.
     * <pre>
     * [23] XMLDecl ::= '&lt;?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
     * [24] VersionInfo ::= S 'version' Eq (' VersionNum ' | " VersionNum ")
     * [80] EncodingDecl ::= S 'encoding' Eq ('"' EncName '"' |  "'" EncName "'" )
     * [32] SDDecl ::= S 'standalone' Eq (("'" ('yes' | 'no') "'")
     *                 | ('"' ('yes' | 'no') '"'))
     * </pre> 
     */
    protected void scanXMLDecl() throws IOException, SAXException {
        // TODO
    } // scanXMLDecl()

    /** 
     * Scans multiple miscellaneous sections.
     * <pre>
     * [27] Misc ::= Comment | PI |  S
     * </pre> 
     */
    protected void scanMisc() throws IOException, SAXException {
        if (DEBUG) System.out.println(">>> scanMisc()");

        while (true) {
            fEntityScanner.skipSpaces();
            /***
            int c = fEntityScanner.scanChar();
            if (c == '<') {
                c = fEntityScanner.peekChar();
                if (c == '?') {
                    fEntityScanner.scanChar();
                    scanPI();
                    continue;
                }
                if (c == '!') {
                    fEntityScanner.scanChar();
                    c = fEntityScanner.scanChar();
                    if (c != '-') {
                        throw new SAXException("expected '-', found '"+(char)c+'\'');
                    }
                    c = fEntityScanner.scanChar();
                    if (c != '-') {
                        throw new SAXException("expected '-', found '"+(char)c+'\'');
                    }
                    scanComment();
                    continue;
                }
                // REVISIT: Assuming that it's an element. -Ac
                return;

            }
            throw new SAXException("expected '<', found '"+(char)c+'\'');
            /***/
            int c = fEntityScanner.peekChar();
            if (c == '<') {
                if (fEntityScanner.skipString("<?")) {
                    scanPI();
                    continue;
                }
                if (fEntityScanner.skipString("<!--")) {
                    scanComment();
                    continue;
                }
                break;
            }
            // REVISIT: report error
            throw new SAXException("expected '<', found '"+(char)c+'\'');
            /***/
        }

        if (DEBUG) System.out.println("<<< scanMisc()");
    } // scanMisc()

    /** 
     * Scans a comment.
     * <pre>
     * [15] Comment ::= '&lt;!--' ((Char - '-') | ('-' (Char - '-')))* '-->'
     * </pre> 
     */
    protected void scanComment() throws IOException, SAXException {
        if (DEBUG) System.out.println(">>> scanComment()");

        // data
        fStringBuffer.clear();
        while (fEntityScanner.scanData("--", fString)) {
            fStringBuffer.append(fString);
        }
        fStringBuffer.append(fString);
        if (fEntityScanner.scanChar() != '>') {
            // REVISIT: report error
            throw new SAXException("comment cannot contain \"--\"");
        }

        // call handler
        if (fDocumentHandler != null) {
            fDocumentHandler.comment(fStringBuffer);
        }

        if (DEBUG) System.out.println("<<< scanComment()");
    } // scanComment()

    /** 
     * Scans a processing instruction.
     * <pre>
     * [16] PI ::= '&lt;?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'
     * [17] PITarget ::= Name - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
     * </pre>
     * <p>
     * <strong>Note:</strong> This method assumes that the leading
     * "&lt;?" characters have been consumed. 
     */
    protected void scanPI() throws IOException, SAXException {
        if (DEBUG) System.out.println(">>> scanPI()");

        // target
        String target = fEntityScanner.scanName();

        // data
        XMLString data = fString;
        if (fEntityScanner.scanData("?>", fString)) {
            do {
                fStringBuffer.append(fString);
            } while (fEntityScanner.scanData("?>", fString));
            fStringBuffer.append(fString);
            data = fStringBuffer;
        }

        // call handler
        if (fDocumentHandler != null) {
            fDocumentHandler.processingInstruction(target, data);
        }

        if (DEBUG) System.out.println("<<< scanPI()");
    } // scanPI()

    /** 
     * Scans the DOCTYPE declaration.
     * <pre>
     * [28] doctypedecl ::= '&lt;!DOCTYPE' S Name (S ExternalID)? S?
     *                      ('[' (markupdecl | PEReference | S)* ']' S?)? '>'
     * </pre> 
     */
    protected void scanDoctypeDecl() throws IOException, SAXException {
        // TODO
    } // scanDoctypeDecl()

    /** 
     * Scans an element.
     * <p>
     * <pre>
     * [39] element ::= EmptyElemTag | STag content ETag
     * [44] EmptyElemTag ::= '<' Name (S Attribute)* S? '/>'
     * [40] STag ::= '<' Name (S Attribute)* S? '>'
     * [42] ETag ::= '</' Name S? '>'
     * </pre> 
     * <p>
     * <strong>Note:</strong> This method assumes that the leading
     * '&lt;' character has been consumed.
     */
    protected void scanElement() throws IOException, SAXException {
        if (DEBUG) System.out.println(">>> scanElement()");

        //
        // Start element
        //

        // name
        if (fNamespaces) {
            fEntityScanner.scanQName(fElemQName);
        }
        else {
            String name = fEntityScanner.scanName();
            fElemQName.setValues(null, name, name, null);
        }
        String rawname = fElemQName.rawname;

        // attributes
        boolean empty = false;
        fAttributes.clear();
        do {
            // spaces
            fEntityScanner.skipSpaces();

            // end tag?
            int c = fEntityScanner.peekChar();
            if (c == '>') {
                fEntityScanner.scanChar();
                break;
            }
            else if (c == '/') {
                fEntityScanner.scanChar();
                c = fEntityScanner.scanChar();
                if (c != '>') {
                    // REVISIT: report error
                    throw new SAXException("expected '>'");
                }
                empty = true;
                break;
            }
            else if (!XMLChar.isNameStart(c)) {
                // REVISIT: report error
                throw new SAXException("expected attribute name, found '"+(char)c+'\'');
            }
                
            // attributes
            scanAttribute(fAttributes);

        } while (true);

        // handle namespaces
        String uri = null;
        if (fNamespaces) {
            // push namespace context
            fNamespaceSupport.pushContext();

            // bind namespaces
            bindNamespaces(fElemQName, fAttributes);
            uri = fElemQName.uri;
        }

        // call handler
        if (fDocumentHandler != null) {
            fDocumentHandler.startElement(fElemQName, fAttributes);
        }

        //
        // Content
        //

        if (!empty) {
            scanContent();
        }

        //
        // End element
        //

        if (!empty) {
            // slash
            int c = fEntityScanner.scanChar();
            if (c != '/') {
                // REVISIT: report error
                throw new SAXException("expected '/'");
            }

            // name
            if (fNamespaces) {
                fEntityScanner.scanQName(fElemQName);
            }
            else {
                String name = fEntityScanner.scanName();
                fElemQName.setValues(null, name, name, uri);
            }

            // end
            fEntityScanner.skipSpaces();
            c = fEntityScanner.scanChar();
            if (c != '>') {
                // REVISIT: report error
                throw new SAXException("expected '>'");
            }

            // tags must be balanced
            if (!fElemQName.rawname.equals(rawname)) {
                // REVISIT: report error
                throw new SAXException("expected \""+rawname+"\""+
                                       " but found \""+fElemQName.rawname+'"');
            }
        }

        // call handler
        if (fDocumentHandler != null) {
            fDocumentHandler.endElement(fElemQName);
        }

        // handle namespaces
        if (fNamespaces) {
            // call handler
            if (fDocumentHandler != null) {
                Enumeration prefixes = fNamespaceSupport.getDeclaredPrefixes();
                while (prefixes.hasMoreElements()) {
                    String prefix = (String)prefixes.nextElement();
                    fDocumentHandler.endPrefixMapping(prefix);
                }
            }

            // pop context
            fNamespaceSupport.popContext();
        }

        if (DEBUG) System.out.println("<<< scanElement()");
    } // scanElement()

    /** 
     * Scans an attribute.
     * <p>
     * <pre>
     * [41] Attribute ::= Name Eq AttValue
     * [10] AttValue ::= '"' ([^<&"] | Reference)* '"' | "'" ([^<&'] | Reference)* "'"
     * </pre> 
     * <p>
     * <strong>Note:</strong> This method assumes that the next 
     * character on the stream is the first character of the attribute
     * name.
     *
     * @param attributes The attributes list for the scanned attribute.
     */
    protected void scanAttribute(XMLAttributes attributes) 
        throws IOException, SAXException {
        if (DEBUG) System.out.println(">>> scanAttribute()");

        // name
        if (fNamespaces) {
            fEntityScanner.scanQName(fAttrQName);
        }
        else {
            String name = fEntityScanner.scanName();
            fAttrQName.setValues(null, name, name, null);
        }

        // equals
        fEntityScanner.skipSpaces();
        int equals = fEntityScanner.scanChar();
        if (equals != '=') {
            // REVISIT: report error
            throw new SAXException("expected '=', found '"+(char)equals+'\'');
        }
        fEntityScanner.skipSpaces();

        // quote
        int oquote = fEntityScanner.scanChar();
        if (oquote != '\'' && oquote != '"') {
            // REVISIT: report error
            throw new SAXException("expected open quote, found '"+(char)oquote+'\'');
        }

        // content
        final String CDATA = fSymbolTable.addSymbol("CDATA");
        attributes.setAttribute(fAttrQName, CDATA, null);
        XMLString value = fString;
        if (fEntityScanner.scanAttContent(oquote, fString)) {
            fStringBuffer.clear();
            do {
                fStringBuffer.append(fString);
                /***
                if (fEntityScanner.peekChar() == '&') {
                    // TODO: handle entities in value
                }
                /***/
            } while (fEntityScanner.scanAttContent(oquote, fString));
            fStringBuffer.append(fString);
            value = fStringBuffer;
        }
        attributes.setValue(attributes.getLength() - 1, value);

        // quote
        int cquote = fEntityScanner.scanChar();
        if (cquote != cquote) {
            // REVISIT: report error
            throw new SAXException("expected close quote");
        }

        if (DEBUG) System.out.println("<<< scanAttribute()");
    } // scanAttribute()

    /**
     * Scans a reference.
     * <pre>
     * [67] Reference ::= EntityRef | CharRef
     * [68] EntityRef ::= '&' Name ';'
     * [66] CharRef ::= '&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';'
     * </pre>
     */
    protected void scanReference() throws IOException, SAXException {
        // TODO
    } // scanReference()

    /** 
     * Scans element content.
     * <pre>
     * [43] content ::= (element | CharData | Reference | CDSect | PI | Comment)*
     * </pre> 
     */
    protected void scanContent() throws IOException, SAXException {

        while (true) {
            boolean more = false;
            do {
                more = fEntityScanner.scanContent(fString);
                if (fDocumentHandler != null) {
                    // REVISIT: check for whitespace
                    fDocumentHandler.characters(fString, false);
                }
            } while (more);
            int c = fEntityScanner.scanChar();
            if (c == '<') {
                c = fEntityScanner.peekChar();
                if (c == '/') {
                    break;
                }
                if (c == '!') {
                    fEntityScanner.scanChar();
                    c = fEntityScanner.scanChar();
                    if (c == '-') {
                        fEntityScanner.scanChar();
                        if (fEntityScanner.scanChar() != '-') {
                            // REVISIT: report error
                            throw new SAXException("expected '-'");
                        }
                        scanComment();
                        continue;
                    }
                    if (c == '[') {
                        // TODO
                        continue;
                    }
                    throw new SAXException("what to do now?");
                }
                if (c == '?') {
                    scanPI();
                    continue;
                }
                scanElement();
            }
            else if (c == '&') {
                scanReference();
            }
            else {
                break;
            }
        }

    } // scanContent()

    // utility methods
    
    /**
     * Binds the namespaces for the start of an element. This method
     * searchs for namespace binding attributes and adds the URI
     * information to the element and attribute qualified names.
     */
    protected void bindNamespaces(QName element, XMLAttributes attributes) 
        throws SAXException {

        // search for new namespace bindings
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            String rawname = attributes.getQName(i);
            if (rawname.startsWith("xmlns")) {
                // declare prefix in context
                String prefix = rawname.length() > 5
                              ? attributes.getLocalName(i) : "";
                String uri = attributes.getValue(i);
                fNamespaceSupport.declarePrefix(prefix, uri);

                // call handler
                if (fDocumentHandler != null) {
                    fDocumentHandler.startPrefixMapping(prefix, uri);
                }
            }
        }

        // bind the element
        String prefix = element.prefix != null
                      ? element.prefix : "";
        element.uri = fNamespaceSupport.getURI(prefix);
        if (element.prefix == null && element.uri != null) {
            element.prefix = fSymbolTable.addSymbol("");
        }
        if (element.prefix != null && element.uri == null) {
            // REVISIT: report error
            throw new SAXException("element prefix \""+element.prefix+"\" not bound");
        }

        // bind the attributes
        for (int i = 0; i < length; i++) {
            attributes.getName(i, fAttrQName);
            String rawname = attributes.getQName(i);
            if (rawname.startsWith("xmlns")) {
                fAttrQName.uri = NamespaceSupport.XMLNS;
            }
            else {
                if (fAttrQName.prefix != null) {
                    fAttrQName.uri = fNamespaceSupport.getURI(fAttrQName.prefix);
                    if (fAttrQName.uri == null) {
                        // REVISIT: report error
                        throw new SAXException("attribute prefix \""+fAttrQName.prefix+"\" is not bound");
                    }
                }
                else {
                    // attributes with no prefix get element's uri
                    fAttrQName.uri = element.uri;
                }
            }
            attributes.setName(i, fAttrQName);
        }

    } // bindNamespaces(QName,XMLAttributes)

    //
    // XMLComponent methods
    //

    /**
     * 
     * 
     * @param componentManager The component manager.
     *
     * @throws SAXException Throws exception if required features and
     *                      properties cannot be found.
     */
    public void reset(XMLComponentManager componentManager)
        throws SAXException {

        // get features
        fNamespaces = componentManager.getFeature(NAMESPACES);
        fAttributes.setNamespaces(fNamespaces);

        // get properties
        fSymbolTable = (SymbolTable)componentManager.getProperty(SYMBOL_TABLE);
        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER);
        fEntityManager = (XMLEntityManager)componentManager.getProperty(ENTITY_MANAGER);
        /*** REVISIT: Add DTD support. ***
        fDTDScanner = (XMLDTDScanner)componentManager.getProperty("http://apache.org/xml/properties/internal/dtd-scanner");
        /***/

    } // reset(XMLComponentManager)

    /**
     * Sets the state of a feature during parsing.
     * 
     * @param featureId 
     * @param state 
     */
    public void setFeature(String featureId, boolean state)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        /*** NOTE: Namespaces cannot be set during parse. ***
        final String SAX_FEATURES = "http://xml.org/sax/features/";
        if (featureId.startsWith(SAX_FEATURES)) {
            final String feature = featureId.substring(SAX_FEATURES.length());
            if (feature.equals("namespaces")) {
                fNamespaces = state;
            }
            return;
        }
        /***/

    } // setFeature(String,boolean)

    /**
     * Sets the value of a property during parsing.
     * 
     * @param propertyId 
     * @param value 
     */
    public void setProperty(String propertyId, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        
        final String INTERNAL_PROPERTY = "http://apache.org/xml/properties/internal/";
        if (propertyId.startsWith(INTERNAL_PROPERTY)) {
            final String property = propertyId.substring(INTERNAL_PROPERTY.length());
            if (property.equals("symbol-table")) {
                fSymbolTable = (SymbolTable)value;
            }
            else if (property.equals("error-reporter")) {
                fErrorReporter = (XMLErrorReporter)value;
            }
            else if (property.equals("entity-manager")) {
                fEntityManager = (XMLEntityManager)value;
            }
            return;
        }

    } // setProperty(String,Object)

    //
    // XMLDocumentSource methods
    //

    /**
     * setDocumentHandler
     * 
     * @param documentHandler 
     */
    public void setDocumentHandler(XMLDocumentHandler documentHandler) {
        fDocumentHandler = documentHandler;
    } // setDocumentHandler(XMLDocumentHandler)

} // class XMLDocumentScanner
