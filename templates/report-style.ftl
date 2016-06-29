<#include "report-macros.ftl">

::selection {
	background: #f46f5d;
}

body {
   font-size: 14px;
   text-align: justify;
   word-wrap: normal;
   font-variant: normal;
   font-family: Helvetica, Verdana, Arial, sans-serif;
   margin:-1px;
}

h1 {
   font-family: Helvetica, Verdana, Arial, sans-serif;
   font-variant: normal;
   color: #030371;
   font-size: 24px;
}

h2 {
   font-family: Helvetica, Verdana, Arial, sans-serif;
   color: #2f2f2f;
   font-size: 13px;
   text-transform: uppercase;
}


ul {
   margin-bottom: -1px;
   font-family: Helvetica, Verdana, Arial, sans-serif;
   font-size: 13px;
   list-style-image: url(data:image/png;base64,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);
}

table {
   padding-top: 10px;
   padding-bottom: 10px;
   width: 85%;
   border-spacing: 16px 2px;
   background-color: #f3f3f3;
   border-right-width: 1px;
   border-bottom-width: 1px;
   border-top-width: 1px;
   border-left-width: 5px;
   border-right-color: #6c0f0f;
   border-bottom-color: #6c0f0f;
   border-top-color: #6c0f0f;
   border-left-color: #2f2f2f;
   border-style: solid;
   /* border-color: #6c0f0f; */
   font-size: 13px;
   font-family: Helvetica, Verdana, Arial, sans-serif;
   
}

tr {
   font-family: Helvetica, Verdana, Arial, sans-serif;
   border-width: 1px;
}

td {
   border-width: 1px;
}

hr {
   align: top;
}

div.summary-title-block {
  text-align: center;
  margin-top: 10%;
}

div.summary-table {
  width: 50%;
  margin-left: 25%;
  margin-right: 25%;
  margin-top: 10%;
}
 
div.summary-table table {
  border-bottom-width: 1px;
  border-left-width: 1px;
  border-right-width: 1px;
  border-top-width: 1px; 
  width: 100%;
 
}

span.table-title {
  display: table;
  margin: 0 auto;
  text-align: center;
  font-size: 16px;
  font-weight: heavy;  
}

tr.severity-row {
  border-style: solid;
  border-top-width: 1px;
  border-bottom-width: 1px;
  font-weight: bold;
  background: #FFFFFF;
}

td.severity-HIGH {
   background-color: #ff0f06;
   color: #ffffff;
   font-weight: bold;
   text-align: center;
}

td.severity-MEDIUM {
   background-color: #ff7c24;
   color: #ffffff;
   text-align: center;
}

td.severity-LOW {
   background-color: #61aa00;
   color: #ffffff;
   text-align: center;
}

td.severity-INFO {
   background-color: #535ad7;
   color: #ffffff;
   text-align: center;
}

td.severity-UNKNOWN {
   text-align: center;
}

td.alert-count {
   text-align: center;
}

span.summary-bottom {
   left: 0;
   bottom: 0;
   position: absolute;
   margin-bottom: 1%;
}

span.footertext {
   font-family: Helvetica, Verdana, Arial, sans-serif;
   font-size: 11px;
} 

div.summary-page {
  width 100%;
  height: 100%;
  overflow: auto;
  font-family: Helvetica, Verdana, Arial, sans-serif;
}

div.summary-title-block  {
  font-weight: bold;
  text-align: center;
}

div.summary-title-block h1 {
  font-size: 18px;
  color: #000000;
}

div.summary-title-block h2 {
  font-size: 16px;
}

div.alert-nav {
   float: right;
   padding-top: 2px;
   padding-bottom: 2px;
   padding-right: 2px;
   left: -5px;
}

div.alert-body {
   width: 100%;
   overflow: auto;
   height: 100%;
}

div.alert-nav a{
   background-color: #c20006;
   color: #ffffff;   
   width: 60px;
   pading-left: 2px;
   padding-right: 2px;
   border-top-width: 1px;
   border-left-width: 1px;
   border-right-width: 1px;
   border-bottom-width: 1px;
   border-style: solid;
   border-color: #000000;
   font-size: 13px;
   font-family: Helvtica, Verdana, Arial, sans-serif;
}

div.detailed-findings {
   background-color: #c20006;
   color: #ffffff;
   width: 20%;
   margin-left: 40%;
   margin-right: 40%;
   margin-top: 10%;
   pading-left: 2px;
   padding-right: 2px;
   border-top-width: 1px;
   border-left-width: 1px;
   border-right-width: 1px;
   border-bottom-width: 1px;
   border-style: solid;
   border-color: #000000;
   font-size: 16px;
   font-family: Helvtica, Verdana, Arial, sans-serif;
   text-align: center;
}

div.detailed-findings a{
   color: #FFFFFF;
}

div.tablefield {
   font-weight: bold;
}

span.table-title {

  width: 20%;
  margin-left: 40%;
  margin-right: 40%;
  margin-bottom: 5%;
  text-align: center;
  
}

span.attackstring {
   font-family: monospace;
}

span.resourcecontent {
   font-family: monospace;
}

span.responsecontent {
   font-family: monospace;
   height: 200px;
   width: 40%;
   overflow-y: scroll;
   overflow-x: scroll;
}

span.parameter {
   font-style: italic;
}

span.highrisk {
   background-color: #ff0f06;
   color: #ffffff;
   padding-left: 3px;
   padding-right: 3px;
   padding-top:2px;
   padding-bottom: 2px;
   font-weight: bold;
}

span.medrisk {
   background-color: #ff7c24;
   color: #ffffff;
   padding-left: 3px;
   padding-right: 3px;
   padding-top:2px;
   padding-bottom: 2px;
}

span.lowrisk {
   background-color: #61aa00;
   color: #ffffff;
   padding-left: 3px;
   padding-right: 3px;
   padding-top:2px;
   padding-bottom: 2px;
}

span.inforisk {
   background-color: #535ad7;
   color: #ffffff;
   padding-left: 3px;
   padding-right: 3px;
   padding-top:2px;
   padding-bottom: 2px;
}

div.reflink {
   line-height: 28px;
   text-transform: none;
}

a {
   text-decoration: none;
   font-family: Helvetica, Verdana, Arial, sans-serif;
   color: #a20b0b;
   font-variant: normal;
   font-weight: bold;
}

a.visited {
   text-decoration: none;
}

div.section {
   padding-left: 20px;
   padding-bottom: 20px;
   padding-top: 8px;
   border-width: 1px;
   border-style: none;

   background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA8AAABQCAYAAADGDi9oAAAACXBIWXMAAAsTAAALEwEAmpwYAAAKT2lDQ1BQaG90b3Nob3AgSUNDIHByb2ZpbGUAAHjanVNnVFPpFj333vRCS4iAlEtvUhUIIFJCi4AUkSYqIQkQSoghodkVUcERRUUEG8igiAOOjoCMFVEsDIoK2AfkIaKOg6OIisr74Xuja9a89+bN/rXXPues852zzwfACAyWSDNRNYAMqUIeEeCDx8TG4eQuQIEKJHAAEAizZCFz/SMBAPh+PDwrIsAHvgABeNMLCADATZvAMByH/w/qQplcAYCEAcB0kThLCIAUAEB6jkKmAEBGAYCdmCZTAKAEAGDLY2LjAFAtAGAnf+bTAICd+Jl7AQBblCEVAaCRACATZYhEAGg7AKzPVopFAFgwABRmS8Q5ANgtADBJV2ZIALC3AMDOEAuyAAgMADBRiIUpAAR7AGDIIyN4AISZABRG8lc88SuuEOcqAAB4mbI8uSQ5RYFbCC1xB1dXLh4ozkkXKxQ2YQJhmkAuwnmZGTKBNA/g88wAAKCRFRHgg/P9eM4Ors7ONo62Dl8t6r8G/yJiYuP+5c+rcEAAAOF0ftH+LC+zGoA7BoBt/qIl7gRoXgugdfeLZrIPQLUAoOnaV/Nw+H48PEWhkLnZ2eXk5NhKxEJbYcpXff5nwl/AV/1s+X48/Pf14L7iJIEyXYFHBPjgwsz0TKUcz5IJhGLc5o9H/LcL//wd0yLESWK5WCoU41EScY5EmozzMqUiiUKSKcUl0v9k4t8s+wM+3zUAsGo+AXuRLahdYwP2SycQWHTA4vcAAPK7b8HUKAgDgGiD4c93/+8//UegJQCAZkmScQAAXkQkLlTKsz/HCAAARKCBKrBBG/TBGCzABhzBBdzBC/xgNoRCJMTCQhBCCmSAHHJgKayCQiiGzbAdKmAv1EAdNMBRaIaTcA4uwlW4Dj1wD/phCJ7BKLyBCQRByAgTYSHaiAFiilgjjggXmYX4IcFIBBKLJCDJiBRRIkuRNUgxUopUIFVIHfI9cgI5h1xGupE7yAAygvyGvEcxlIGyUT3UDLVDuag3GoRGogvQZHQxmo8WoJvQcrQaPYw2oefQq2gP2o8+Q8cwwOgYBzPEbDAuxsNCsTgsCZNjy7EirAyrxhqwVqwDu4n1Y8+xdwQSgUXACTYEd0IgYR5BSFhMWE7YSKggHCQ0EdoJNwkDhFHCJyKTqEu0JroR+cQYYjIxh1hILCPWEo8TLxB7iEPENyQSiUMyJ7mQAkmxpFTSEtJG0m5SI+ksqZs0SBojk8naZGuyBzmULCAryIXkneTD5DPkG+Qh8lsKnWJAcaT4U+IoUspqShnlEOU05QZlmDJBVaOaUt2ooVQRNY9aQq2htlKvUYeoEzR1mjnNgxZJS6WtopXTGmgXaPdpr+h0uhHdlR5Ol9BX0svpR+iX6AP0dwwNhhWDx4hnKBmbGAcYZxl3GK+YTKYZ04sZx1QwNzHrmOeZD5lvVVgqtip8FZHKCpVKlSaVGyovVKmqpqreqgtV81XLVI+pXlN9rkZVM1PjqQnUlqtVqp1Q61MbU2epO6iHqmeob1Q/pH5Z/YkGWcNMw09DpFGgsV/jvMYgC2MZs3gsIWsNq4Z1gTXEJrHN2Xx2KruY/R27iz2qqaE5QzNKM1ezUvOUZj8H45hx+Jx0TgnnKKeX836K3hTvKeIpG6Y0TLkxZVxrqpaXllirSKtRq0frvTau7aedpr1Fu1n7gQ5Bx0onXCdHZ4/OBZ3nU9lT3acKpxZNPTr1ri6qa6UbobtEd79up+6Ynr5egJ5Mb6feeb3n+hx9L/1U/W36p/VHDFgGswwkBtsMzhg8xTVxbzwdL8fb8VFDXcNAQ6VhlWGX4YSRudE8o9VGjUYPjGnGXOMk423GbcajJgYmISZLTepN7ppSTbmmKaY7TDtMx83MzaLN1pk1mz0x1zLnm+eb15vft2BaeFostqi2uGVJsuRaplnutrxuhVo5WaVYVVpds0atna0l1rutu6cRp7lOk06rntZnw7Dxtsm2qbcZsOXYBtuutm22fWFnYhdnt8Wuw+6TvZN9un2N/T0HDYfZDqsdWh1+c7RyFDpWOt6azpzuP33F9JbpL2dYzxDP2DPjthPLKcRpnVOb00dnF2e5c4PziIuJS4LLLpc+Lpsbxt3IveRKdPVxXeF60vWdm7Obwu2o26/uNu5p7ofcn8w0nymeWTNz0MPIQ+BR5dE/C5+VMGvfrH5PQ0+BZ7XnIy9jL5FXrdewt6V3qvdh7xc+9j5yn+M+4zw33jLeWV/MN8C3yLfLT8Nvnl+F30N/I/9k/3r/0QCngCUBZwOJgUGBWwL7+Hp8Ib+OPzrbZfay2e1BjKC5QRVBj4KtguXBrSFoyOyQrSH355jOkc5pDoVQfujW0Adh5mGLw34MJ4WHhVeGP45wiFga0TGXNXfR3ENz30T6RJZE3ptnMU85ry1KNSo+qi5qPNo3ujS6P8YuZlnM1VidWElsSxw5LiquNm5svt/87fOH4p3iC+N7F5gvyF1weaHOwvSFpxapLhIsOpZATIhOOJTwQRAqqBaMJfITdyWOCnnCHcJnIi/RNtGI2ENcKh5O8kgqTXqS7JG8NXkkxTOlLOW5hCepkLxMDUzdmzqeFpp2IG0yPTq9MYOSkZBxQqohTZO2Z+pn5mZ2y6xlhbL+xW6Lty8elQfJa7OQrAVZLQq2QqboVFoo1yoHsmdlV2a/zYnKOZarnivN7cyzytuQN5zvn//tEsIS4ZK2pYZLVy0dWOa9rGo5sjxxedsK4xUFK4ZWBqw8uIq2Km3VT6vtV5eufr0mek1rgV7ByoLBtQFr6wtVCuWFfevc1+1dT1gvWd+1YfqGnRs+FYmKrhTbF5cVf9go3HjlG4dvyr+Z3JS0qavEuWTPZtJm6ebeLZ5bDpaql+aXDm4N2dq0Dd9WtO319kXbL5fNKNu7g7ZDuaO/PLi8ZafJzs07P1SkVPRU+lQ27tLdtWHX+G7R7ht7vPY07NXbW7z3/T7JvttVAVVN1WbVZftJ+7P3P66Jqun4lvttXa1ObXHtxwPSA/0HIw6217nU1R3SPVRSj9Yr60cOxx++/p3vdy0NNg1VjZzG4iNwRHnk6fcJ3/ceDTradox7rOEH0x92HWcdL2pCmvKaRptTmvtbYlu6T8w+0dbq3nr8R9sfD5w0PFl5SvNUyWna6YLTk2fyz4ydlZ19fi753GDborZ752PO32oPb++6EHTh0kX/i+c7vDvOXPK4dPKy2+UTV7hXmq86X23qdOo8/pPTT8e7nLuarrlca7nuer21e2b36RueN87d9L158Rb/1tWeOT3dvfN6b/fF9/XfFt1+cif9zsu72Xcn7q28T7xf9EDtQdlD3YfVP1v+3Njv3H9qwHeg89HcR/cGhYPP/pH1jw9DBY+Zj8uGDYbrnjg+OTniP3L96fynQ89kzyaeF/6i/suuFxYvfvjV69fO0ZjRoZfyl5O/bXyl/erA6xmv28bCxh6+yXgzMV70VvvtwXfcdx3vo98PT+R8IH8o/2j5sfVT0Kf7kxmTk/8EA5jz/GMzLdsAAAAgY0hSTQAAeiUAAICDAAD5/wAAgOkAAHUwAADqYAAAOpgAABdvkl/FRgAAAEVJREFUeNrs1LENACAIRUE07r/ydwdpMDn61xyElST1OLsaIxaLR8Wn8QtoOxJgVkUbGG3atGnTFluV+KMjuQAAAP//AwA8ACKSl0uMsQAAAABJRU5ErkJggg==);

   background-repeat: repeat-x;
   background-position: left bottom;

   font-size: 12px;
}

div.section h2{
   padding-left: 25px;
   <@sectionIcon
       vars.redArrowURL ! ""
   />
}

div.content#sectioncontent {
   padding-left: 40px;
   width: 85%;
}

div.vegabanner {
   <@bgPattern
     vars.bannerPatternURL ! ""
   />
   height: 25px;
}

div.vegabanner img {
   float: left;
   position: relative;
   left: -2px;
}


div.vegabanner span {
   font-family: Helvetica, Verdana, Arial, sans-serif;
   font-size: 11px;
   float: right;
   position:relative;
   top: 5px;
   left: -5px;
   color: #ffffff;
   letter-spacing: 3px;
}

div.topsection {
   border-style: none;
   <@titleBgPattern
       vars.titlePatternURL ! ""
   />
   position: relative;
   height: 65px;
   border-top-style: solid;
   border-width: 1px;
   border-color: #ffffff;
}

div.footer a{
   background-color: #c20006;
   color: #ffffff;
   font-family:Helvetica, Verdana, Arial, sans-serif;
   font-size: 10px;
   float: right;
   margin-right: 50px;
   margin-bottom: 20px;
   padding-right: 5px;
   padding-left: 5px;
   padding-top: 3px;
   padding-bottom: 3px;
}

span.title {
   position: relative;
   bottom: -40px;
   left: 45px;
   text-overflow: clip;
   color: #ffffff;
   font-family: Helvetica, Verdana, Arial, sans-serif;
   font-size: 18px;
   font-weight: lighter;
   letter-spacing: 1px;
}

div.section#referencesection ul{
     <@linkBullet
     vars.linkArrowURL ! ""
   />
}

a.reflink {
   font-size: 12px;
   color: #4c00ff;
   text-decoration: none;
}

a.innerlink {
   color: #5f2674;
   text-decoration: none;
}

a.innerlinksmall {
   font-size: 12px;
   color: #4c00ff;
   text-decoration: none;
}

.bannerLogo {
   content:url(data:image/png;base64,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);
}
