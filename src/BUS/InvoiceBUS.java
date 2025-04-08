
package BUS;

import DAO.*;
import DTO.*;
import GUI.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author ASUS
 */
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
        jPanelInvoice.showListInvoice(result);
        jPanelInvoice.showListInvoiceDetail(null);
    }

    public JFreeChart createChart(){
        return InvoiceDAO.createInvoiceChart();
    }
}
