<#include "macros.ftl">

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
   <@bullet
     vars.bulletPointURL ! ""
   />
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

div.tablefield {
   font-weight: bold;
}

span.attackstring {
   font-family: monospace;
}

span.resourcecontent {
   font-family: monospace;
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
   <@greyGradient
     vars.sectionGradientURL ! ""
   />
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