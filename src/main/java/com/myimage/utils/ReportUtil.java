/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myimage.utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
 
import net.sf.jasperreports.engine.JRException;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
 
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
 
public class ReportUtil {
 
    public static final String TEMPLATE = "/reports/users.jrxml";
     
    public StreamedContent geraRelatorio(HashMap parametrosRelatorio) throws Exception {
         
        StreamedContent arquivoRetorno = null;
 
        try {
            Connection conexao = this.getConexao();                
            InputStream reportStream = this.getClass().getResourceAsStream(TEMPLATE);
            JasperDesign jd = JRXmlLoader.load(reportStream);
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, parametrosRelatorio, conexao);
             
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jp));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
            exporter.exportReport();
 
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             
            arquivoRetorno = new DefaultStreamedContent(bais, "pdf", "users.pdf");
             
        } catch (JRException e) {
            e.printStackTrace();
            throw new Exception("Não foi possível gerar o relatório.", e);
        } catch (FileNotFoundException e) {
            throw new Exception("Arquivo do relatório nõo encontrado.", e);
        }
        return arquivoRetorno;
    }
 
    private Connection getConexao() throws Exception {
        java.sql.Connection conexao = null;
        try {
            Context initContext = new InitialContext();
            
            javax.sql.DataSource ds = (javax.sql.DataSource) initContext.lookup("jdbc/myimage");
            conexao = (java.sql.Connection) ds.getConnection();
        } catch (NamingException e) {
            throw new Exception("Não foi possível encontrar o nome da conexão do banco.", e);
            
        } catch (SQLException e) {
            //throw new Exception("Ocorreu um erro de SQL.", e);
            System.out.println("Erro" + e );
        }
        return conexao;
    }
}
