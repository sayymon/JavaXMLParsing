package com.alura.curso.java.xml.parsing;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConversaoXMLTest {

	@Test
	public void testConversaoXMLUsandoDOMElement() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
		
		//Habilita a validação utilizando o XSD
		fabrica.setValidating(true);
		// fala para o factory considerar o XSD na validaçao para parsear o documento
		fabrica.setNamespaceAware(true);
		fabrica.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
		
		DocumentBuilder builder = fabrica.newDocumentBuilder();
		Document document = builder.parse("src/vendas.xml");
		
		Element venda = document.getDocumentElement();
		String moeda = venda.getAttribute("moeda");
		System.out.println(moeda);
		NodeList formasDePagamento = document.getElementsByTagName("formaDePagamento");
		Element fdp = (Element)formasDePagamento.item(0);
		System.out.println(fdp.getTextContent());
		
		NodeList produtos = document.getElementsByTagName("produto");
		for (int i = 0;i <  produtos.getLength();i++) {
			Element produtoElement = (Element)produtos.item(i);
			String nome = produtoElement.getElementsByTagName("nome").item(0).getTextContent();
			Double preco = Double.parseDouble(produtoElement.getElementsByTagName("preco").item(0).getTextContent());
			Produto produto = new Produto(nome, preco);
			System.out.println(produto);
		}
		
		
		
	}

}
