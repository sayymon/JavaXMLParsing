package com.alura.curso.java.xml.parsing;

import java.util.List;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * 
 * Para ler o arquivo XML, precisamos ter uma classe que representa a nossa estratégia de leitura, onde vamos implementar a lógica de cada evento. Para isso, crie a classe ProdutosHandler, apenas com o objetivo de exibir todos os produtos. Não esqueça que para ler todo o texto, precisamos de um StringBuilder, além disso a nossa classe ProdutosHandler deve herdar de DefaultHandler.
 * 
 * @author Saymon
 *
 */
public class ProdutosHandler extends DefaultHandler{
	private StringBuilder conteudo;
	private Produto produto;
	private List<Produto> produtos = new ArrayList<Produto>();
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		System.out.println("abriu a tag:"+qName);
		if (qName.equals("produto")) {
			produto = new Produto();
		}
		conteudo = new StringBuilder();
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		conteudo.append(new String(ch,start,length));
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("produto")) {
			produtos.add(produto);
		}else if (qName.equals("nome")) {
			produto.setNome(conteudo.toString());
		}else if (qName.equals("preco")) {
			produto.setPreco(new Double(conteudo.toString()));
		}
	}

	public List<Produto> getProdutos() {
		return produtos;
	}

}
