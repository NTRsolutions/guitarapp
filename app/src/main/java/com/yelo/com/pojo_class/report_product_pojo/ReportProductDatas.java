package com.yelo.com.pojo_class.report_product_pojo;

/**
 * Created by hello on 13-Jun-17.
 */

public class ReportProductDatas
{
    /*"_id":"5937d48cb765f3154ba1fd17",
            "reportReason":"It's prohibited on Yelo",
            "reasonDate":1496831116602*/

    private String _id="",reportReason="",reasonDate="",reportReasonByUser="";
    private boolean isItemSelected=false;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }

    public String getReasonDate() {
        return reasonDate;
    }

    public void setReasonDate(String reasonDate) {
        this.reasonDate = reasonDate;
    }

    public String getReportReasonByUser() {
        return reportReasonByUser;
    }

    public void setReportReasonByUser(String reportReasonByUser) {
        this.reportReasonByUser = reportReasonByUser;
    }

    public boolean isItemSelected() {
        return isItemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        isItemSelected = itemSelected;
    }
}
