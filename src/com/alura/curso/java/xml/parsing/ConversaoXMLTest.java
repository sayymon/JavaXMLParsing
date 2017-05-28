package com.alura.curso.java.xml.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ConversaoXMLTest {

	/**
	 * Aula 3 comentou que essa forma � lenta, mas � a base para leitura de qualquer XML criada pela W3C
	 * 
	 */
	@Test
	public void testConversaoXMLUsandoDOMElement() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
		
		//Habilita a valida��o utilizando o XSD
		fabrica.setValidating(true);
		// fala para o factory considerar o XSD na valida�ao para parsear o documento
		fabrica.setNamespaceAware(true);
		fabrica.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
		
		DocumentBuilder builder = fabrica.newDocumentBuilder();
		//Pois neste ponto ele carrega a estrutura do xml inteira 
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
	/**
	Essa implementa��o � mais performatica, pois n�o aloca memoria e 
	 flexibiliza as a��es durante a leitura do xml
	 ,pois implementamos uma classe que vai ficar ouvindo alguns eventos, 
	como startElement, endElement, caracteres e assim por diante. 
	A grande desvantagem � que, caso precisemos, 
	n�o ser� poss�vel voltar para alguma parte do documento XML, 
	pois o evento � disparado apenas uma vez por tag.
	<br>
	<h1>Quando Usar o SAX ao inv�s do DOM</h1>
	<br>
	Quando temos um arquivo muito grande no qual apenas uma parte interessa, n�o faz sentido carreg�-lo inteiro na mem�ria para usar apenas um peda�o. Portanto, ficar ouvindo todos os eventos, mas apenas tratar os que interessam, passa a ser uma abordagem interessante, pois economiza mem�ria, j� que baseado em eventos n�s decidimos o que deve permanecer nela.
	
	*/
	@Test
	public void testConverteXmlUtilizandoSax() throws Exception{
		XMLReader leitor = XMLReaderFactory.createXMLReader();
		ProdutosHandler produtosHandler = new ProdutosHandler();
		leitor.setContentHandler(produtosHandler);
		InputStream ips = new FileInputStream("src/vendas.xml");
		InputSource is = new InputSource(ips);
		leitor.parse(is);
		System.out.println(produtosHandler.getProdutos());
	}
	
	/**
	 * Ao contrario do SAX o STAX ao invez de esperar o evento de ler a tag ele que estimula a leitura
	 * Em quest�o de performance os dois s�o equivalentes, dependendo da necessidade um pode ser mais adequado do que o outro
	 * 
	 * A forma que vimos nesse cap�tulo foi o StAX, que possui um funcionamento bem parecido com o SAX. Tamb�m � baseado em eventos, por�m enquanto no SAX a pr�pria API invoca os m�todos que definimos no nosso handler, no StAX conseguimos pegar uma "lista" de todos os eventos e percorr�-los um a um. A vantagem disso � que, por ser uma "lista", � poss�vel voltar para o evento anterior, caso necess�rio.
	 * 
	 * Por ser uma estrat�gia baseada em eventos, o arquivo tamb�m n�o � carregado na mem�ria, portanto � interessante para situa��es nas quais apenas uma parte do arquivo � �til.
	 * @throws Exception
	 */
	@Test
	public void testConverteXmlUtilizandoSTax() throws Exception{
		InputStream is = new FileInputStream("src/vendas.xml");
		XMLEventReader eventos = XMLInputFactory.newInstance().createXMLEventReader(is);
		List<Produto> produtos = new ArrayList<Produto>();
		Produto produto = null;
		while (eventos.hasNext()) {
			XMLEvent evento = eventos.nextEvent();
			if(evento.isStartElement() && evento.asStartElement().getName().getLocalPart().equals("produto")){
				produto = new Produto();
			}else if (evento.isStartElement() && evento.asStartElement().getName().getLocalPart().equals("nome")) {
				evento = eventos.nextEvent();
				produto.setNome(evento.asCharacters().getData());
			}else if (evento.isStartElement() && evento.asStartElement().getName().getLocalPart().equals("preco")) {
				evento = eventos.nextEvent();
				produto.setPreco(new Double(evento.asCharacters().getData()));
			}else if(evento.isEndElement() && evento.asEndElement().getName().getLocalPart().equals("produto")){
				produtos.add(produto);
			}
		}
		
		System.out.println(produtos);
		
	}	
	
	/**
	 * O XPath nos permite selecionar apenas uma parte do nosso documento, usando uma sintaxe bem parecida com a estrutura de pastas do nosso sistema operacional. O que facilita a busca de dados do nosso documento.
	 */
	@Test
	public void testExemploXPathComDOM() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException{

		DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
		
		//Habilita a valida��o utilizando o XSD
		fabrica.setValidating(true);
		// fala para o factory considerar o XSD na valida�ao para parsear o documento
		fabrica.setNamespaceAware(true);
		fabrica.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
		
		DocumentBuilder builder = fabrica.newDocumentBuilder();
		//Pois neste ponto ele carrega a estrutura do xml inteira 
		Document document = builder.parse("src/vendas.xml");
		
		//XPATH criando filtro no XML
		//String expressionXpath = "venda/produtos/produto[1]";
		//String expressionXpath = "venda/produtos/produto[nome='Livro de Xml']";
		String expressionXpath = "venda/produtos/produto[contains(nome,'Livro')]";
		XPathExpression expression = XPathFactory.newInstance().newXPath().compile(expressionXpath);
		
		NodeList produtos = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
		
		Element venda = document.getDocumentElement();
		String moeda = venda.getAttribute("moeda");
		System.out.println(moeda);
		NodeList formasDePagamento = document.getElementsByTagName("formaDePagamento");
		Element fdp = (Element)formasDePagamento.item(0);
		System.out.println(fdp.getTextContent());
		
		for (int i = 0;i <  produtos.getLength();i++) {
			Element produtoElement = (Element)produtos.item(i);
			String nome = produtoElement.getElementsByTagName("nome").item(0).getTextContent();
			Double preco = Double.parseDouble(produtoElement.getElementsByTagName("preco").item(0).getTextContent());
			Produto produto = new Produto(nome, preco);
			System.out.println(produto);
		}
	
		
	}
	/**
	 * Arquivos XSLT s�o arquivos que nos ajudam a transformar arquivos XML em outros formatos, como o HTML, facilitando essa convers�o. Por�m n�o � muito utilizado atualmente no mercado, pois geralmente arquivos XSLT crescem muito r�pido, ficando dif�ceis de manter.
	 * @throws Exception
	 */
	@Test
	public void testConversoXmlParaHtmlUsandoXslt() throws Exception{
		InputStream xlsIs = new FileInputStream("src/xmlParahtml.xsl");
		StreamSource xlsSource = new StreamSource(xlsIs);
		InputStream vendasIs = new FileInputStream("src/vendas.xml");
		StreamSource vendasSource = new StreamSource(vendasIs);
		Transformer transformer = TransformerFactory.newInstance().newTransformer(xlsSource);
		StreamResult vendasHtmlResult = new StreamResult("src/vendas.html");
		transformer.transform(vendasSource, vendasHtmlResult);
	}
	
	/**
	 * O JAXB � especifica��o do Java que nos permite associar diretamente uma classe a um arquivo XML. Para isso precisamos criar uma classe que representa o nosso XML, adicionar a anota��o @XmlRootElement e por fim usar a classe JAXBContext para parsear o documento.
	 * @throws Exception
	 */
	@Test
	public void testConversaoXMLParaObjComJAXB() throws Exception{
		JAXBContext jaxbContext = JAXBContext.newInstance(Venda.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Venda venda = (Venda)unmarshaller.unmarshal(new File("src/vendas.xml"));
		
		System.out.println(venda);
	}
	
	/**
	 * A principal desvantagem do JAX-B � que os arquivos tamb�m s�o interpretados usando �rvores, ou seja, possu�mos os mesmos problemas do DOM. Quando o arquivo � muito grande, acabamos gastando muita mem�ria, portanto o JAX-B � interessante para quando temos arquivos pequenos e vamos usar todas as informa��es deste arquivo.
	 * @throws Exception
	 */
	@Test
	public void testConversaoObjParaXMLComJAXB() throws Exception{
		JAXBContext jaxbContext = JAXBContext.newInstance(Venda.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		Venda venda = new Venda();
		venda.setFormaDePagamento("Debito");
		venda.getProdutos().addAll(Arrays.asList(new Produto("Livro de XML", 29.99),
												 new Produto("Livro de OO", 29.99))
								   );
		StringWriter sw = new StringWriter();
		marshaller.marshal(venda, sw);
		System.out.println(sw.toString());
		
	}	
	//https://github.com/alura-cursos/xml-java/archive/master.zip
	
}
