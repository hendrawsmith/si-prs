<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<h1><s:property value="%{pageTitle}"/></h1>
<s:form>

<s:actionerror/>
<s:actionmessage/>
<s:textfield id="kkksPilih" name="kkks" label="Pilih KKKS" cssClass="multiple" data-multiple="kkksMultiple" size="100" required="1"/>
<span style="display:none" separator="," id="kkksMultiple">
	<s:iterator value="%{KkksList}">
		<span value="<s:property value='%{value}'/>" key="<s:property value='%{key}'/>"></span>
	</s:iterator>
</span>
<s:textfield name="emailto" label="Send To" cssClass="multiple" data-multiple="emailtoMultiple" size="100" required="1"/>
<span style="display:none" separator="," id="emailtoMultiple" name="emailtoMultiple" >
	<s:iterator value="%{MailToList}" id="emailFromControler" >
		<span value="<s:property value='%{value}'/>" key="<s:property value='%{key}'/>"></span>
	</s:iterator>
</span>
<s:textarea name="subject" label="Subject E-mail" rows="2" cols="100" required="1"/>
<s:textarea name="content" label="Isi E-mail" rows="5" cols="100" required="1"/>

<s:submit/>
</s:form>
<script type="text/javascript">
    $(document).ready(function () {
    	$("#kkksPilih").on('change',function(){
    		var a = $("#kkksPilih").val();
    		alert(a);
    
	    		$.ajax({
		         type: "POST",
		         dataType: "html",
		         url: "link buat nembak ke view dan query",
		         data: "kks="+kks,
		         success: function(msg){
		        	 
		         }
		      });  	
	    		
    	});
    	
    });
</script>