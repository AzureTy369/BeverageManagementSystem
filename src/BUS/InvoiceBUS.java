package BUS;

import DAO.InvoiceDAO;
import DTO.Invoice;
import DTO.ListInvoice;
import GUI.InvoicePanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.jfree.chart.JFreeChart;

public class InvoiceBUS implements ActionListener {
    private ListInvoice listIv;
    private InvoicePanel jPanelInvoice;

    public InvoiceBUS(ListInvoice listIv, InvoicePanel jPanelInvoice) {
        this.listIv = listIv;
        this.jPanelInvoice = jPanelInvoice;
    }

    public InvoiceBUS() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jPanelInvoice.getButtonInvoiceSearch()) {
            search();
        }
    }

    private void search() { 
        String info = jPanelInvoice.getTextFieldSearch().getText().trim();
        ArrayList<Invoice> result = listIv.search(info);
        if (result == null) {
            result = new ArrayList<>();
        }
        jPanelInvoice.showListInvoice(result);
        jPanelInvoice.showListInvoiceDetail(null);
        jPanelInvoice.selectedRowIndex = -1;
    }

    public void loadInvoices() {
        ArrayList<Invoice> result = InvoiceDAO.getAllInvoices();
        listIv.setList(result);
        jPanelInvoice.showListInvoice(result);
        jPanelInvoice.showListInvoiceDetail(null);
        jPanelInvoice.selectedRowIndex = -1;
    }

    public JFreeChart createChart() {
        return InvoiceDAO.createInvoiceChart();
    }
}