package com.bpmigas.pma.dashboard.secman;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.criterion.Restrictions;

import com.bpmigas.pma.dashboard.Constants;
import com.bpmigas.pma.dashboard.data.aset.PraFuppHeader;
import com.bpmigas.pma.dashboard.db.HibernateTools;
import com.bpmigas.pma.dashboard.db.InitException;
import com.sun.corba.se.impl.orbutil.closure.Constant;
import com.bpmigas.pma.dashboard.data.Companies;

public class WebUser implements Serializable {
	private static final long serialVersionUID = 4533732007501602988L;
	private static final Log log = LogFactory.getLog(WebUser.class);

	private static Date lastRoleUpdate = new Date();
	public static void updateAllRole() { lastRoleUpdate = new Date(); }
	
	private Date lastUpdate = new Date();
	
	/** identifier field */
	private String domain;
	
	@ManyToOne(targetEntity=Companies.class, optional=false, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="COMPANYID")
	@Fetch(FetchMode.JOIN)
	protected Companies Companies;

	/** identifier field */
	private String username;
	
	private String role;
	
	private WebRole webRole = null;
	public String getRole() {
		return role;
	}

	@ManyToOne
	private WebUserJob job;
	
	public WebRole getWebRole() {
		try {
			if (webRole == null || lastUpdate == null || (lastRoleUpdate != null && lastUpdate.before(lastRoleUpdate))) {
				webRole = (WebRole) HibernateTools.getCurrentSession().get(WebRole.class, role);
				lastUpdate = new Date();
				if (webRole == null) {
					// TODO: hack karena padding menjadi 20 bytes di oracle CHAR(20)
					webRole = (WebRole) HibernateTools.getCurrentSession().get(WebRole.class, String.format("%1$-20s",getRole()));
					if (webRole == null)
						log.error("No WebRole found for user " + getUsernameWithDomain() + " (ROLE:" + getRole() + ")");
				}
			}
			return webRole;
		} catch (InitException e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}

	public void setRole(String role) {
		this.role = role;
		this.webRole = null;
	}
	
	public void setWebRole(WebRole webRole) {
		setRole(webRole.getRole());
	}

	/** nullable persistent field */
	private String password, password2, password3;

	/** nullable persistent field */
	private String category;

	/** nullable persistent field */
	private String email;

	/** nullable persistent field */
	private String description;

	/** nullable persistent field */
	private String longname;

	/** nullable persistent field */
	private String department;

	/** nullable persistent field */
	private String jobtitle;

	/** nullable persistent field */
	private String phone;

	/** nullable persistent field */
	private java.util.Date pdate;

	/** nullable persistent field */
	private String welcomePage;

	/** full constructor */
	public WebUser(String username, String domain, String password, String category,
			String email, String description, String longname, String department,
			String jobtitle, String phone, Date pdate, String welcomePage) {
		this.domain = domain;
		this.username = username;
		this.password = password;
		this.category = category;
		this.email = email;
		this.description = description;
		this.longname = longname;
		this.department = department;
		this.jobtitle = jobtitle;
		this.phone = phone;
		this.pdate = pdate;
		this.welcomePage = welcomePage;
	}

	/** default constructor */
	public WebUser() {
	}

	/** minimal constructor */
	public WebUser(String username, String domain) {
		this.domain = domain.toUpperCase();
		this.username = username.toUpperCase();
	}
	
	/**
	 * Mendapatkan KKKS dari user (jika ada)
	 * @return string kkks sesuai dengan tabel COMPANIES, atau null jika bukan user KKKS
	 */
	public String getKKKS() {
		String domain = getDomain().toLowerCase().trim();
		boolean isKKKS = !(domain.equals("multi") || domain.equals("bpmigas") || domain.equals("djmgb") || domain.equals("skkmigas") || domain.equals(Constants.DJKN_DOMAIN.toLowerCase()) || domain.equals(Constants.PPBMN_DOMAIN.toLowerCase()));
		return isKKKS ? getDomain() : null;
	}
	
	public String getDomain() {
		return this.domain;
	}
	
	public String getUsernameWithDomain() {
		if (!getUsername().startsWith(getDomain() + "\\"))
			return getDomain() + "\\" + getUsername();
		else
			return getUsername();
	}

	public void setDomain(String domain) {
		this.domain = domain.toUpperCase();
	}
	public void fixDomain() {
		if (this.domain != null && !this.domain.equals("MULTI"))
			if (this.username != null && !this.username.startsWith(this.domain + "\\"))
				this.username = this.domain + "\\" + this.username;
	}
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username.toUpperCase();
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword2() {
		return this.password2;
	}

	public void setPassword2(String password) {
		this.password2 = password;
	}
	
	public String getPassword3() {
		return this.password3;
	}

	public void setPassword3(String password) {
		this.password3 = password;
	}
	
	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLongname() {
		return this.longname;
	}

	public void setLongname(String longname) {
		this.longname = longname;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getJobtitle() {
		return this.jobtitle;
	}

	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getPdate() {
		return this.pdate;
	}

	public void setPdate(Date pdate) {
		this.pdate = pdate;
	}

	public String toString() {
		return new ToStringBuilder(this).
				append("domain", getDomain()).
				append("username", getUsername()).
				toString();
	}

	public String getWelcomePage() {
		return welcomePage == null ? isKKKS() ? "kkks_home" : "bpmigas_home" : welcomePage;
	}

	public void setWelcomePage(String welcomePage) {
		this.welcomePage = welcomePage;
	}

	public boolean isKKKS() {
		return getKKKS() != null;
	}
	
	/**
	 * Mengecek privilege
	 * @param action action yang akan dicek
	 * @param option
	 * @return true hanya jika user memiliki akses ke action dan option
	 */
	public boolean getPrivilege(String action, String option) {
		return getWebRole().checkWebActionOption(action, option);
	}
	public void checkPrivilege(String action, String option) {
		if (!getPrivilege(action, option))
			throw new AuthorizationException();
	}
	/**
	 * Mengembalikan list option yang digrant untuk action tertentu
	 * 
	 * @param action action yang dites
	 * @return null jika tidak punya akses sama sekali ke action
	 */
	public String[] getActionPrivileges(String action) {
		WebAction waction = getWebRole().getWebAction(action);
		if (waction == null) return null;
		return waction.getOptionsArray();
	}
	
	public static WebUser getWebUser(String username) {
		Session s = HibernateTools.getCurrentSessionNoException();
		@SuppressWarnings("unchecked")
		List<WebUser> l = (List<WebUser>) s.createCriteria(WebUser.class).add(Restrictions.eq("username", username)).list();
		if (l.size() != 0)
			return l.get(0);
		
		else {
			String[] uname = username.split("\\\\", 2);
			if (uname.length > 1) {
				return (WebUser) s.get(WebUser.class, new WebUser(uname[0], uname[1]));
			}
			return null;
		}
	}
	
	public WebUserJob getJob() {
		return job;
	}
	
	public void setJob(WebUserJob job) {
		this.job = job;
	}
	
	public void setJobId(Long jobId) {
		this.job = (WebUserJob) HibernateTools.getCurrentSessionNoException().get(WebUserJob.class, jobId);
	}
	
	public static Map<String, String> getSearchList() {
		return HibernateTools.queryAsMap("select email, domain, username from WebUser where role like '%KKKS%' ORDER BY domain");
	}
	
	public static Map<String, String> getDomainList() {
		return HibernateTools.queryAsMap("select v.email,c.companyName from WebUser v left join v.Companies c");
	}
}
