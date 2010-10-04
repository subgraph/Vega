<#ftl strip_text=true>
<#escape x as x?html>

<#function defined x>
  <#return !(x?is_boolean)>
</#function>

<#macro topsection title image>
  <div class="topsection" id="titlesection">
  <#if defined(image)>
    <img src="${image}" width=80px height=80px>
  </#if>
  <span class="title">${title}</span>
  </div>
</#macro>

<#macro infobox class resource severity severityCSS>
<div class="section" id="infobox">
<h2>At a glance</h2>

<table>
  <#if defined(class)>
  <tr>
    <td><div class="tablefield">Classification</div></td><td><a class="innerlink" href="#description">${class}</a></td>
  </tr>
  </#if>
  
  <#if defined(resource)>
  <tr>
  	<td><div class="tablefield">Resource</div></td><td><a class="resourcelink" href="#">${resource}</a></td>
  </tr>
  </#if>

  <#if defined(severity) && defined(severityCSS)>
  <tr>
  	<td><div class="tablefield">Risk</div></td><td><span class="${severityCSS}">${severity}</span></td>
  </tr>
  </#if>
  
</table>

</div>

</#macro>

<#macro impactsection impact>
<#if defined(impact)>
<div class="section" id = "impactsection">
  <h2>Impact</h2>
  <ul>
  <#list impact as item>
    <li>${item}</li>
  </#list>
  </ul>
</div>
</#if>
</#macro>

<#macro remediation text>
<#if defined(text)>
<div class="section" id="remediationsection">
<h2>Remediation</h2>
${text}
</div>
</#if>
</#macro>

<#macro detailsection resource methods parameter attackstring>
<#if defined(resource) || defined(methods) || defined(parameter) || defined(attackstring)>
<div class="section" id="detailsection">
<h2>Detailed Findings</h2>
<table>
<#if defined(resource)>
<tr>
	<td><div class="tablefield">Resource</div></td><td><a class="resourcelink" href="#">${resource}</a></td>
</tr>
</#if>
<#if defined(methods)>
<tr>
	<td><div class="tablefield">Methods</div></td><td>${methods}</td>
</tr>
</#if>
<#if defined(parameter)>
<tr>
	<td><div class="tablefield">Parameter</div></td><td><span class="parameter">${parameter}</span></td>
</tr>
</#if>
<#if defined(attackstring)>
<tr>
	<td><div class="tablefield">Attack string</div></td><td><span class="attackstring">${attackstring}</span></td>
</tr>
</#if>
</table>
</div>
</#if>
</#macro>

<#macro discussion text>
<#if defined(text)>
<div class="section" id="description">
<h2>Discussion</h2>
${text}
</div>
</#if>
</#macro>


</#escape>