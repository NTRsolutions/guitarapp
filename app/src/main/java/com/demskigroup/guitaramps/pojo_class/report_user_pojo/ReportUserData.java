package com.demskigroup.guitaramps.pojo_class.report_user_pojo;

/**
 * Created by hello on 28-Jul-17.
 */

class ReportUserData
{
    /*"_id":"5937d48cb765f3154ba1fd17",
            "reportReason":"It's prohibited on Yelo",
            "reasonDate":1496831116602*/

    private String _id="",reportReason="",reasonDate="";

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
}
