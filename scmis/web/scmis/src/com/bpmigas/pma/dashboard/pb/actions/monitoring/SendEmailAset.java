package com.bpmigas.pma.dashboard.pb.actions.monitoring;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bpmigas.pma.dashboard.CommonFunction;
import com.bpmigas.pma.dashboard.Config;
import com.bpmigas.pma.dashboard.actions.MainAction;
import com.bpmigas.pma.dashboard.data.Companies;
import com.bpmigas.pma.dashboard.secman.WebUser;
import com.bpmigas.pma.dashboard.data.Report;
import com.bpmigas.pma.dashboard.data.ReportList;
import com.bpmigas.pma.dashboard.db.HibernateTools;
import com.bpmigas.pma.dashboard.db.InitException;
import com.bpmigas.pma.dashboard.pb.actions.monitoring.ViewReportSummaryValidasiMonthly.ReportValidasi;
import com.bpmigas.pma.dashboard.services.MailServices;

public class SendEmailAset extends MainAction {
	private static final long serialVersionUID = 5872785272882498283L;
	private static Log log = LogFactory.getLog(SendEmailAset.class);
	private String emailto;
	private String customSetting;
	private boolean sendAlsoToKkks;
	private String kkks;
	private boolean allKkks;
	private String subject;
	private String content;
	
	public String getEmailto() {
		return emailto;
	}

	public void setEmailto(String emailto) {
		this.emailto = emailto;
	}

	public String getCustomSetting() {
		return customSetting;
	}
	
	public void setCustomSetting(String customSetting) {
		this.customSetting = customSetting;
	}
	
	public boolean isSendAlsoToKkks() {
		return sendAlsoToKkks;
	}
	
	public void setSendAlsoToKkks(boolean sendAlsoToKkks) {
		this.sendAlsoToKkks = sendAlsoToKkks;
	}

	public String getKkks() {
		return kkks;
	}
	
	public void setKkks(String kkks) {
		this.kkks = kkks;
	}
	
	public boolean isAllKkks() {
		return allKkks;
	}
	
	public void setAllKkks(boolean allKkks) {
		this.allKkks = allKkks;
	}
		
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String getPageTitle() {
		return "Send Email Aset";
	}

	@Override
	public String getPageBody() {
		return "/web_mail_aset.jsp";
	}

	public SendEmailAset() {
		setWebSecurityActionName("ViewReport");
	}
	
	@Override
	public String execute() throws Exception {
		checkUserPrivilege("sr");
		if (emailto != null) {
			//Session s = HibernateTools.getCurrentSession();
			//List<String> kkksSent = SendReminderAset(getEmailto(), isSendAlsoToKkks(), getKkksAsArray(), getContent(),getSubject());
			//addActionMessage("Sent e-mail for: " + StringUtils.join(kkksSent.iterator(), ", "));
			//s.save(kkksSent);

			MailServices.sendMail(getEmailto(), getSubject(), getContent());
			return "DONE";
		}
	
		
		return super.execute();
	}
	
	private String[] getKkksAsArray() {
		if (kkks == null || allKkks)
			return null;
		if ("".equals(kkks.trim()))
			return null;
		return kkks.split(", ");
	}
	
	public Map<String, String> getKkksList() {
		return Companies.getSearchEmailList();
	}
	
	@SuppressWarnings("unused")
	private String[] getemailtoAsArray() {
		if (emailto == null)
			return null;
		if ("".equals(emailto.trim()))
			return null;
		return emailto.split(", ");
	}
	
	public Map<String, String> getMailToList() {
		return WebUser.getSearchList();
	}

	public static List<String> SendReminderAset(String string, boolean b, String[] strings, String string3, String string4) throws InitException {
		return SendReminderAset(null, null, true, null);
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	/**
	 * Mengirimkan email reminder ke KKKS.
	 * 
	 * @param email e-mail tujuan (menjadi CC dan Reply-To jika sendToKkks bernilai true).
	 *              default (diisi null) mengacu pada ValueList REMINDERMAIL 
	 * @param customSetting kalau ingin custom setting dari email
	 * @param sendToKkks true kalau mengirim langsung ke KKKS, jika tidak akan mengirim ke \link(email)
	 * @param kkks daftar KKKS yang dikirim. null akan mengirim untuk semua KKKS yang belum melapor.
	 * @throws InitException
	 */
	public static List<String> SendReminderAset(String email, String customSetting, boolean sendToKkks, String[] kkks) throws InitException {
		Session s = HibernateTools.getCurrentSession();

		List<ReportList> reports = s.createCriteria(ReportList.class)
				.add(Restrictions.eq("reportType", (byte) 1))
				.addOrder(Order.asc("orderNum"))
				 .addOrder(Order.asc("reportNo")).list();
		LinkedHashMap<String, ReportValidasi> ret = new LinkedHashMap<String, ViewReportSummaryValidasiMonthly.ReportValidasi>(
				reports.size());
		for (ReportList r : reports) {
			ret.put(r.getReportNo(),
					new ReportValidasi(r.getReportNo(), r.getReportName(),
							null, null));
		}
		
		Date periode = CommonFunction.addMonth(new Date(), -1);
		periode.setDate(1);
		
		List<String> kkksSent = new ArrayList<String>();
		
		Criteria crt = s.createCriteria(Companies.class);
		if (kkks != null) {
			crt.add(Restrictions.in("companyId", kkks));
		}
		for (Companies c: (List<Companies>) crt.addOrder(Order.asc("companyId")).list()) {
			log.info("Sending Reminder for " + c.getCompanyId());
			for (ReportValidasi r: ret.values()) {
				r.setSubmitBy(null);
				r.setSubmitDate(null);
			}
			for (Report r : (List<Report>) s
					.createCriteria(Report.class)
					.add(Restrictions.in("reportNo", ret.keySet()))
					.add(Restrictions.eq("companyId", c.getCompanyId()))
					.add(Restrictions.ge("periode", periode))
					.add(Restrictions.lt("periode",
							CommonFunction.addDay(periode, 1)))
					.add(Restrictions.isNotNull("approveDate"))
					.add(Restrictions.isNotNull("approveBy"))
					.addOrder(Order.asc("reportNo"))
					.addOrder(Order.asc("submitDate")).list()) {
				ReportValidasi rv = ret.get(r.getReportNo());
				rv.setSubmitBy(r.getSubmitBy());
				rv.setSubmitDate(r.getSubmitDate());
			}
			boolean isAll = true;
			for (ReportValidasi r: ret.values()) {
				if (r.getSubmitDate() == null) {
					isAll = false;
					break;
				}
			}
			if (isAll)
				continue;
			
			StringBuilder b = new StringBuilder();
			b.append(
				"Bapak / Ibu sampai saat ini kami belum menerima laporan bulanan "+
				"di SCM Integrated System dari "+ c.getCompanyId() +". Mohon segera melaporkan paling lambat "+
				"tanggal 20 pada setiap bulannya."+
				"<html><head>" +
				"<style type='text/css'>" +
				"table.std {" +
				"	border-collapse: collapse !important;" +
				"	border: 1px solid #EEE !important;" +
				"}" +
				"" +
				"table.std td {" +
				"	font-weight: normal !important;" +
				"	padding: .4em !important;" +
				"	border-top-width: 0px !important;" +
				"}" +
				"" +
				"table.std td.right {" +
				"	text-align: right !important;" +
				"}" +
				"table.std td.center {" +
				"	text-align: center !important;" +
				"}" +
				"" +
				"table.std td,table.std th {" +
				"	padding: 3px 7px 2px 7px !important;" +
				"	vertical-align: top !important;" +
				"}" +
				"" +
				"table.std th {" +
				"	text-align: center !important;" +
				"	padding-top: 5px !important;" +
				"	padding-bottom: 4px !important;" +
				"	color: #fff !important;" +
				"	background: #A7C942 url(../images/ui-bg_highlight-soft_50_a7c942_1x100.png) 50% 50% repeat-x !important;" +
				"}" +
				"" +
				"table.std th a {" +
				"	color: #fff !important;" +
				"}" +
				"" +
				"table.std th a:hover,table.std td a:hover {" +
				"	text-decoration: underline !important;" +
				"}" +
				"" +
				"table.std tr.alt td {" +
				"	background-color:#EEFFBD !important;" +
				"}" +
				"" +
				"table.std tr.hover td {" +
				"	background-color:#DEFF7A !important;" +
				"}" +
				"</style>" +
				"</head><body>" +
				"<div style=\"text-align:left\">" +
				"<p>Status pelaporan KKKS " + c.getCompanyId() + ":</p>" +
				"<table class=\"std\" style=\"width:auto;border: 1px solid #EEE;\">" +
					"<tr>" +
						"<th>Kode KKKS</th>" +
						"<td>" + c.getCompanyId() + " (Kode SIPM: " + c.getSipmCode() + ")</td>" +
						"	</tr>" +
						"	<tr class='alt'>" +
						"		<th>Nama KKKS</th>" +
						"		<td>" + c.getCompanyName() + "</td>" +
						"	</tr>" +
						"	<tr>" +
						"		<th>E-Mail Kontak</th>" +
						"		<td>" + c.getMainContactEmail() + "</td>" +
						"	</tr>" +
						"	<tr class='alt'>" +
						"		<th>Status</th>" +
						"		<td>" + c.getCompanyTypeString() + "</td>" +
						"	</tr>" +
						"	<tr>" +
						"		<th>Region / Wilayah</th>" +
						"		<td>" + c.getRegion() + " / " + c.getWilayah() + "</td>" +
						"	</tr>" +
						"</table>" +
						"<p>Status pelaporan " + CommonFunction.formatLongMonth(periode)+
						"</p>" +
						"<table class=\"std\" style=\"width:auto;border: 1px solid #EEE;\" id=\"validasi\">" +
						"	<thead>" +
						"		<tr style='border: 1px solid #EEE'>" +
						"			<th>Laporan</th>" +
						"			<th>Deskripsi</th>" +
						"			<th>&#x2713;</th>" +
						"			<th>Tanggal Laporan /<br/> Revisi Terakhir</th>" +
						"			<th>Oleh</th>" +
						"		</tr>" +
						"		</thead>" +
						"		<tbody>");
			int i = 0;
			for (ReportValidasi r: ret.values()) {
				b.append("<tr style='border: 1px solid #EEE'" + (i % 2 == 0 ? "" : " class='alt'") + ">" +
					"<td>" + r.getReportNo().startsWith("MP") + "</td>" +
					"<td>" + r.getReportName() + "</td>" +
					"<td>" + (r.getSubmitDate() != null ? "&#x2713;" : "") + "</td>" +
					"<td>" + CommonFunction.format(r.getSubmitDate()) + "</td>" +
					"<td>" + (r.getSubmitBy() == null ? "": r.getSubmitBy()) + "</td>" +
					"</tr>");
				i++;
			}
			b.append("</tbody></table>" +
					"<i>*) This e-mail is autogenerated by SCM Integrated System on " + CommonFunction.formatDateTime(new Date()) + ".</i>" +
						"</body>" +
						"</html>");
			Properties prop = null;
			if (customSetting != null) { 
				prop = new Properties(Config.getMailProperties());
				for(String pair: customSetting.split(",")) {
					String[] p = pair.split("=");
					prop.setProperty(p[0],p[1]);
				}
			}
			String subject = "Reminder Pelaporan Aset SI-PRS " + c.getCompanyId() + " - " + c.getCompanyName() + " (" + CommonFunction.formatLongMonth(periode) + ")";
			if (sendToKkks && c.getMainContactEmail() != null && !"".equals(c.getMainContactEmail())) {
				String[] mails = email.split(",");
				MailServices.sendMail(c.getMainContactEmail(), subject, b.toString(), prop, mails, mails);
			} else {
				MailServices.sendMail(email, subject, b.toString(), prop);
			}
			
			kkksSent.add(c.getCompanyId());
		}
		return kkksSent;
	}
	
	@Override
	public String getPageLeft() {
		return "/menu_sipm.jsp";
	}
}
