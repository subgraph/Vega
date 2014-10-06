<#ftl strip_text=true>
<#escape x as x?html>

<#function defined x>
  <#return (x?is_string && x?length != 0) || (x?is_sequence && x?size != 0) >
</#function>

<#macro vegabanner bannerLogo>
	<div class="vegabanner" id="vegabanner">
	<#if defined (bannerLogo)>
		<img src="${bannerLogo}" width=173px height=24px>
		<span> Open Source Web Security Platform </span>
	</#if>
	</div>
</#macro>


<#macro bgPattern bannerPattern>
    <#if defined(bannerPattern)>
		background-image: url('${bannerPattern}');
		background-repeat: repeat;
    </#if>
</#macro>

<#macro topsection title>
  <div class="topsection" id="titlesection">
  	<span class="title">${title}</span>
  </div>
</#macro>

<#macro titleBgPattern titlePattern>
	<#if defined(titlePattern)>
		background-image: url('${titlePattern}');
		background-repeat: repeat;
    </#if>
</#macro>

<#macro greyGradient sectionGradient>
    <#if defined(sectionGradient)>
		background-image: url('${sectionGradient}');
		background-repeat: repeat-x;
		background-position: left bottom;
    </#if>
</#macro>

<#macro sectionIcon redArrow>
	<#if defined (redArrow)>
		background-image:url('${redArrow}');
		background-repeat: no-repeat;
		background-position: 5px 0px;
	</#if>
</#macro>

<#macro infobox class resource param methods detectiontype severity severityCSS>
<div class="section" id="infobox">
	<h2>At a glance</h2>
	<div class="content" id="sectioncontent">
		<table>
		  <#if defined(class)>
		  <tr>
    		<td><div class="tablefield">Classification</div></td><td><a class="innerlink" href="#description">${class}</a></td>
  		</tr>
  		</#if>
  
 		 <#if defined(resource)>
  		<tr>
  			<td><div class="tablefield">Resource</div></td><td><b>${resource}</b></td>
  		</tr>
  		</#if>

	        <#if defined(param)>
                <tr>
                        <td><div class="tablefield">Parameter</div></td><td><b>${param}</b></td>
                </tr>
                </#if>

 
                 <#if defined(methods)>
                <tr>
                        <td><div class="tablefield">Method</div></td><td><b>${methods}</b></td>
                </tr>
                </#if>


		<#if defined(detectiontype)>
		<tr>
			<td><div class="tablefield">Detection Type</div></td><td><b>${detectiontype}</b></td>
		</tr>
		</#if>

  		<#if defined(severity) && defined(severityCSS)>
  			<tr>
  			<td><div class="tablefield">Risk</div></td><td><span class="${severityCSS}">${severity}</span></td>
  			</tr>
  		</#if>
  		</table>
	</div>
</div>

</#macro>

<#macro outputsection text>
  <#if defined(text)>
        <div class="section" id="resourcecontentsection">
          <h2>Resource Content</h2>
                <div class="content" id="sectioncontent">
                  <table>
                        <tr>
                          <td><span class="resourcecontent"><pre>${text}</pre></span></td>
                        </tr>
                  </table>
                </div>
        </div>
  </#if>
</#macro>


<#macro impactsection impact>
  <#if defined(impact)>
	<div class="section" id = "impactsection">
      <h2>Impact</h2>
        <div class="content" id="sectioncontent">
          <ul>
            <#list impact as item>
              <li>${item}</li>
            </#list>
    	  </ul>
        </div>
    </div>
  </#if>
</#macro>

<#macro remediationsection remediation>
  <#if defined(remediation)>
	<div class="section" id="remediationsection">
	  <h2>Remediation</h2>
		<div class="content" id="sectioncontent">
		  <ul>
  			<#list remediation as item>
    		  <li>${item}</li>
  			</#list>
		  </ul>
		</div>
	</div>
  </#if>
</#macro>

<#macro discussion text>
  <#if defined(text)>
	<div class="section" id="description">
	  <h2>Discussion</h2>
		<div class="content" id="sectioncontent">
		  ${text}
		</div>
	</div>
  </#if>
</#macro>

<#macro bullet bulletPoint>
    <#if defined(bulletPoint)>
		list-style-image: url('${bulletPoint}');
    </#if>
</#macro>

<#macro linkBullet linkArrow>
    <#if defined(linkArrow)>
		list-style-image: url('${linkArrow}');
    </#if>
</#macro>

</#escape>
