package com.alura.curso.java.xml.parsing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Venda {
	private String formaDePagamento;
	
	@XmlElementWrapper(name="produtos")
	@XmlElement(name="produto")
	private List<Produto> produtos;
	public String getFormaDePagamento() {
		return formaDePagamento;
	}
	public void setFormaDePagamento(String formaDePagamento) {
		this.formaDePagamento = formaDePagamento;
	}
	public List<Produto> getProdutos() {
		
		if (produtos == null) {
			produtos = new ArrayList<Produto>();
		}
		return produtos;
	}
	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}
	@Override
	public String toString() {
		return "Venda [formaDePagamento=" + formaDePagamento + ", produtos=" + produtos + "]";
	}
	
	
}
