package org.nexml.model;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Test;
import org.xml.sax.SAXException;


public class TestAnnotationTypes {
	@Test
	public void testXsdType () {
		URI nameSpaceURI = URI.create("http://example.org/knownTypes");
		String xsdUri = "http://www.w3.org/2001/XMLSchema";
		Document doc = DocumentFactory.safeCreateDocument();
		OTUs otus = doc.createOTUs();
		Annotation annotation = null;
		
		// BigDecimal
		annotation = otus.addAnnotationValue("kt:hasBigDecimal", nameSpaceURI, new BigDecimal("0.1"));
		Assert.assertEquals(new QName(xsdUri,"decimal"), annotation.getXsdType());
		Assert.assertEquals(BigDecimal.class, annotation.getValue().getClass());
		Assert.assertEquals(new BigDecimal("0.1"), new BigDecimal(annotation.getValue().toString()));
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
		
		// BigInteger
		annotation = otus.addAnnotationValue("kt:hasBigInteger", nameSpaceURI, new BigInteger("1"));
		Assert.assertEquals(new QName(xsdUri,"integer"), annotation.getXsdType());
		Assert.assertEquals(BigInteger.class, annotation.getValue().getClass());
		Assert.assertEquals(new BigInteger("1"), new BigInteger(annotation.getValue().toString()));
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
		
		// Boolean
		annotation = otus.addAnnotationValue("kt:hasBoolean", nameSpaceURI, true);
		Assert.assertEquals(new QName(xsdUri,"boolean"), annotation.getXsdType());
		Assert.assertEquals(Boolean.class, annotation.getValue().getClass());
		Assert.assertEquals(new Boolean("true"), new Boolean(annotation.getValue().toString()));
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
		
		// Byte
		annotation = otus.addAnnotationValue("kt:hasByte", nameSpaceURI, new Byte("1"));
		Assert.assertEquals(new QName(xsdUri,"byte"), annotation.getXsdType());
		Assert.assertEquals(Byte.class, annotation.getValue().getClass());
		Assert.assertEquals(new Byte("1"), new Byte(annotation.getValue().toString()));
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
		
		// QName
		annotation = otus.addAnnotationValue("kt:hasQName", nameSpaceURI, new QName("foo"));
		Assert.assertEquals(new QName(xsdUri,"QName"), annotation.getXsdType());
		Assert.assertEquals(QName.class, annotation.getValue().getClass());
		Assert.assertEquals(new QName("foo"), new QName(annotation.getValue().toString()));
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
		
		// Double
		annotation = otus.addAnnotationValue("kt:hasDouble", nameSpaceURI, new Double("1.5"));
		Assert.assertEquals(new QName(xsdUri,"double"), annotation.getXsdType());
		Assert.assertEquals(Double.class, annotation.getValue().getClass());
		Assert.assertEquals(new Double("1.5"), new Double(annotation.getValue().toString()));
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
		
		// Float
		annotation = otus.addAnnotationValue("kt:hasFloat", nameSpaceURI, new Float("1.6"));
		Assert.assertEquals(new QName(xsdUri,"float"), annotation.getXsdType());
		Assert.assertEquals(Float.class, annotation.getValue().getClass());
		Assert.assertEquals(new Float("1.6"), new Float(annotation.getValue().toString()));
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
		
		// Long
		annotation = otus.addAnnotationValue("kt:hasLong", nameSpaceURI, new Long("10"));
		Assert.assertEquals(new QName(xsdUri,"long"), annotation.getXsdType());
		Assert.assertEquals(Long.class, annotation.getValue().getClass());		
		Assert.assertEquals(new Long("10"), new Long(annotation.getValue().toString()));	
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
		
		// Short
		annotation = otus.addAnnotationValue("kt:hasShort", nameSpaceURI, new Short("5"));
		Assert.assertEquals(new QName(xsdUri,"short"), annotation.getXsdType());
		Assert.assertEquals(Short.class, annotation.getValue().getClass());
		Assert.assertEquals(new Short("5"), new Short(annotation.getValue().toString()));
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
		
		// String
		annotation = otus.addAnnotationValue("kt:hasString", nameSpaceURI, "bar");
		Assert.assertEquals(new QName(xsdUri,"string"), annotation.getXsdType());
		Assert.assertEquals(String.class, annotation.getValue().getClass());
		Assert.assertEquals("bar", annotation.getValue().toString());
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
		
		// URL (has no xsd type)
		annotation = otus.addAnnotationValue("kt:hasURL", nameSpaceURI, URI.create("http://example.org"));
		Assert.assertEquals(URI.class, annotation.getValue().getClass());
		Assert.assertEquals(URI.create("http://example.org"), URI.create(annotation.getValue().toString()));
		Assert.assertEquals(nameSpaceURI, annotation.getPredicateNamespace());
	}
	
	@Test
	public void parseMetaTypes() {
		String nexmlRoot = System.getenv("NEXML_ROOT");
		if ( nexmlRoot == null ) {
			nexmlRoot = "/Users/rvosa/Dropbox/documents/projects/current/nexml/src/nexml/trunk/nexml";
		}
		File file = new File(nexmlRoot+"/examples/meta_types.xml");
		Document doc = null;
		try {
			doc = DocumentFactory.parse(file);
		} catch (SAXException e) {
			Assert.assertTrue(e.getMessage(), false);
			e.printStackTrace();
		} catch (IOException e) {
			Assert.assertTrue(e.getMessage(), false);
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			Assert.assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
		for ( OTUs otus : doc.getOTUsList() ) {
			for ( Annotation annotation : otus.getAllAnnotations() ) {
				Assert.assertNotSame(Object.class, annotation.getValue().getClass());
				Assert.assertEquals(URI.create("http://example.org/knownTypes"), annotation.getPredicateNamespace());
			}
		}
	}	
}
