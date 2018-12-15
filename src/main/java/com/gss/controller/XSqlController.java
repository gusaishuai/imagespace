package com.gss.controller;

import com.alibaba.fastjson.JSON;
import com.gss.service.XSqlService;
import com.gss.service.common.XSqlExecType;
import com.gss.service.common.XSqlKeyWord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author gusaishuai
 * @since 17/4/30
 */
@Controller
@RequestMapping("/")
public class XSqlController {

    private static final Logger logger = LoggerFactory.getLogger(XSqlController.class);

    private final XSqlService xSqlService;

    @Autowired
    public XSqlController(XSqlService xSqlService) {
        this.xSqlService = xSqlService;
    }

    @ResponseBody
    @RequestMapping("/xsql")
    public String xsql(HttpServletRequest request) {
        try {
            //http://localhost:8080/project/
            String servletPath = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + request.getContextPath() + "/";
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<meta charset=\"utf-8\"/>\n");
            sb.append("<link rel=\"stylesheet\" href=\"/codemirror.css\" />\n");
            sb.append("<link rel=\"stylesheet\" href=\"/show-hint.css\" />\n");
            sb.append("<script src=\"/codemirror.js\"></script>\n");
            sb.append("<script src=\"/sql.js\"></script>\n");
            sb.append("<script src=\"/show-hint.js\"></script>\n");
            sb.append("<script src=\"/sql-hint.js\"></script>\n");
            sb.append("<style type=\"text/css\">\n");
            sb.append("	* {\n");
            sb.append("		font-size: 15px;\n");
            sb.append("	}\n");
            sb.append("	.CodeMirror {\n");
            sb.append("		border-top: 1px solid black;\n");
            sb.append("		border-bottom: 1px solid black;\n");
            sb.append("		font-weight:bold;\n");
            sb.append("	}\n");
            sb.append("	input.button {\n");
            sb.append("		cursor: pointer;\n");
            sb.append("		border:solid#000 1px;\n");
            sb.append("	}\n");
            sb.append("	th {\n");
            sb.append("		border:solid#000 1px;\n");
            sb.append("		background-color: lightgrey;\n");
            sb.append("	}\n");
            sb.append("	td {\n");
            sb.append("		border:solid#000 1px;\n");
            sb.append("	}\n");
            sb.append("	table {\n");
            sb.append("		border-collapse:collapse;\n");
            sb.append("		border:none;\n");
            sb.append("		word-break:break-all;\n");
            sb.append("	}\n");
            sb.append("	#shield {\n");
            sb.append("		width:100%;\n");
            sb.append("		background-color:#DDD;\n");
            sb.append("		position:absolute;\n");
            sb.append("		top:0;\n");
            sb.append("		left:0;\n");
            sb.append("		z-index:2;\n");
            sb.append("		opacity:0.3;\n");
            sb.append("		display:none;\n");
            sb.append("	}\n");
            sb.append("	#toast {\n");
            sb.append("		width:50%;\n");
            sb.append("		height:80%;\n");
            sb.append("		overflow: auto;\n");
            sb.append("		border:solid#000 1px; \n");
            sb.append("		background-color:#FFF;\n");
            sb.append("		margin: auto;\n");
            sb.append("		position: fixed;\n");
            sb.append("		z-index:3;\n");
            sb.append("		top: 0;\n");
            sb.append("		bottom: 0;\n");
            sb.append("		left: 0;\n");
            sb.append("		right: 0;\n");
            sb.append("		display:none;\n");
            sb.append("	}\n");
            sb.append("	.tab-active {\n");
            sb.append("		height: 29px;\n");
            sb.append("	    border-bottom:none;\n");
            sb.append("	    background: #fff;\n");
            sb.append("	    color: #000;\n");
            sb.append("	    font-weight: bold;\n");
            sb.append("	}\n");
            sb.append("	form li {\n");
            sb.append("	    float: left;\n");
            sb.append("	    height: 28px;\n");
            sb.append("	    padding: 0 25px;\n");
            sb.append("	    border: 1px solid #ccc;\n");
            sb.append("	    margin-right: 10px;\n");
            sb.append("	    line-height: 30px;\n");
            sb.append("	    cursor: pointer;\n");
            sb.append("	    background: #FAFAFA;\n");
            sb.append("	    color: #0461B1;\n");
            sb.append("	}\n");
            sb.append("</style>\n");
            sb.append("<script type=\"text/javascript\">\n");
            sb.append("	window.onload = function() {\n");
            sb.append("		window.editor = CodeMirror.fromTextArea(document.getElementById('sql'), {\n");
            sb.append("			mode: 'text/x-mysql',\n");
            sb.append("			indentWithTabs: true,\n");
            sb.append("			smartIndent: true,\n");
            sb.append("			lineNumbers: true,\n");
            sb.append("			autofocus: true,\n");
            sb.append("			lineSeparator: ' ',\n");
            sb.append("			lineWrapping: true,\n");
            sb.append("			extraKeys: {\"Alt-/\": \"autocomplete\"}\n");
            sb.append("		});\n");
            sb.append("     document.getElementById('tables').src = \"").append(servletPath).append("_tableNames\";\n");
            sb.append("	};\n");
            sb.append("	document.onkeydown = function (e) {\n");
            sb.append("		if (e.ctrlKey && e.keyCode === 13) {\n");
            sb.append("			if (document.getElementById('execBtn').disabled === false) {\n");
            sb.append("				exec('exec');\n");
            sb.append("			} else {\n");
            sb.append("				alert('请不要重复执行');\n");
            sb.append("			}\n");
            sb.append("			return false;\n");
            sb.append("		}\n");
            sb.append("	};\n");
            sb.append("	function trim(str){	\n");
            sb.append("		return str.replace(/(^\\s*)|(\\s*$)/g, \"\");\n");
            sb.append("	}\n");
            sb.append("	function exec(type) {\n");
            sb.append("		var sql = trim(editor.getSelection());\n");
            sb.append("		if (sql === '') {\n");
            sb.append("			sql = trim(editor.getValue());\n");
            sb.append("		}\n");
            sb.append("		if (sql === '') {\n");
            sb.append("			alert(\"请输入sql\");\n");
            sb.append("			return;\n");
            sb.append("		}\n");
            sb.append("		var sqlPrefix6 = sql.substring(0, 6).toUpperCase();\n");
            sb.append("		var sqlPrefix4 = sql.substring(0, 4).toUpperCase();\n");
            sb.append("		if (sqlPrefix6 === 'INSERT' || sqlPrefix6 === 'UPDATE' || sqlPrefix6 === 'DELETE') {\n");
            sb.append("			if (type === 'export') {\n");
            sb.append("				alert(\"增删改语句不能导出\");\n");
            sb.append("				return;\n");
            sb.append("			}\n");
            sb.append("			if ((sqlPrefix6 === 'UPDATE' || sqlPrefix6 === 'DELETE') && sql.toUpperCase().indexOf('WHERE') === -1) {\n");
            sb.append("				alert(\"删改语句必须增加WHERE条件\");\n");
            sb.append("				return;\n");
            sb.append("			}\n");
            sb.append("			if (!confirm(\"确认执行增删改语句吗？\")) {\n");
            sb.append("				return;\n");
            sb.append("			}\n");
            sb.append("		} else if (sqlPrefix6 === 'SELECT' || sqlPrefix4 === 'SHOW') {\n");
            sb.append("			if (type === 'export' && !confirm('确认导出吗？')) {\n");
            sb.append("				return;\n");
            sb.append("			}\n");
            sb.append("		} else {\n");
            sb.append("			alert('不合法的语句');\n");
            sb.append("			return;\n");
            sb.append("		}\n");
            sb.append("     if (type === 'exec') {\n");
            sb.append("		    document.getElementById('execBtn').disabled = true;\n");
            sb.append("		}\n");
            sb.append("		document.getElementById('sqlResult').src = '").append(servletPath).append("_xsql?type=' + type + '&sql=' + sql;\n");
            sb.append("	}\n");
            sb.append("	function transport(obj) {\n");
            sb.append("		var toastTable = document.getElementById('toastTable');\n");
            sb.append("		var childNodes = obj.children;\n");
            sb.append("		toastTable.innerHTML = '<tr>';\n");
            sb.append("		for (var i=0;i<childNodes.length;i++) {\n");
            sb.append("			toastTable.innerHTML += '<th width=\"20%\">' + childNodes[i].getAttribute('col') + '</th><td>' + childNodes[i].innerHTML + '</td>';\n");
            sb.append("		}\n");
            sb.append("		toastTable.innerHTML += '</tr>';\n");
            sb.append("		openShield();\n");
            sb.append("	}\n");
            sb.append("	function openShield() {\n");
            sb.append("		document.getElementById(\"shield\").style.height = document.body.scrollHeight + 'px';\n");
            sb.append("		document.getElementById(\"shield\").style.display = \"block\";\n");
            sb.append("		document.getElementById(\"toast\").style.display = \"block\";\n");
            sb.append("	}\n");
            sb.append("	function closeShield() {\n");
            sb.append("		document.getElementById(\"shield\").style.display = \"none\";\n");
            sb.append("		document.getElementById(\"toast\").style.display = \"none\";\n");
            sb.append("	}\n");
            sb.append("	function changeFrameHeight(frame) {\n");
            sb.append("		frame.height = frame.contentWindow.document.documentElement.scrollHeight + 20;\n");
            sb.append("	}\n");
            sb.append("	function change(button) {\n");
            sb.append("		button.style.backgroundColor='lightgrey';\n");
            sb.append("	}\n");
            sb.append("	function restore(button) {\n");
            sb.append("		button.style.backgroundColor='';\n");
            sb.append("	}\n");
            sb.append("	function active(li) {\n");
            sb.append("     	var tags = document.getElementById('tags').children;\n");
            sb.append("     	var contents = document.getElementById('contents').children;\n");
            sb.append("     	for (var i=0;i<tags.length;i++) {\n");
            sb.append("     		if (li.id === tags[i].id) {\n");
            sb.append("     			tags[i].setAttribute('class', 'tab-active');\n");
            sb.append("     			contents[i].style.display = '';\n");
            sb.append("     		} else {\n");
            sb.append("     			tags[i].setAttribute('class', '');\n");
            sb.append("     			contents[i].style.display = 'none';\n");
            sb.append("     		}\n");
            sb.append("     	}\n");
            sb.append("     }\n");
            sb.append("     function query(table) {\n");
            sb.append("     	document.getElementById('columnsResult').src = '").append(servletPath).append("_columns").append("?table=' + table;\n");
            sb.append("     	document.getElementById('indexResult').src = '").append(servletPath).append("_index").append("?table=' + table;\n");
            sb.append("     	document.getElementById('tableResult').src = '").append(servletPath).append("_xsql").append("?type=exec&sql=SELECT * FROM ' + table;\n");
            sb.append("     }\n");
            sb.append("</script>\n");
            sb.append("<head>\n");
            sb.append("	<title>x-sql</title>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("	<form id=\"sqlForm\">\n");
            sb.append("     <table width=\"100%\" style=\"table-layout:fixed;\">\n");
            sb.append("         <tr>\n");
            sb.append("             <td style=\"border:none;padding:10px\" height=\"100%\" width=\"25%\">\n");
            sb.append("		            <iframe id=\"tables\" src=\"").append(servletPath).append("_loading")
                    .append("\" frameborder=\"0\" height=\"100%\" width=\"100%\" style=\"border:1px solid #ccc;\">\n");
            sb.append("		            </iframe>\n");
            sb.append("		        </td>\n");
            sb.append("             <td style=\"border:none;padding:10px\" width=\"75%\">\n");
            sb.append("		            <textarea id=\"sql\"></textarea>\n");
            sb.append("		            <br>\n");
            sb.append("		            <div align=\"right\">\n");
            sb.append("		            <input type=\"button\" id=\"execBtn\" onclick=\"exec('exec')\" value=\"执行[CTRL+ENTER]\" class=\"button\" onmouseover=\"change(this)\" onmouseout=\"restore(this)\"/>&nbsp;&nbsp;\n");
            sb.append("		            <input type=\"button\" id=\"exportBtn\" onclick=\"exec('export')\" value=\"导出\" class=\"button\" onmouseover=\"change(this)\" onmouseout=\"restore(this)\"/>\n");
            sb.append("		            </div>\n");
            sb.append("		        </td>\n");
            sb.append("		    </tr>\n");
            sb.append("		</table>\n");
            sb.append("		<ul id=\"tags\" style=\"height: 30px;list-style: none;margin-bottom: -1px;\">\n");
            sb.append("		    <li id=\"exec\" onclick=\"active(this)\" class=\"tab-active\">执行</li>\n");
            sb.append("		    <li id=\"columns\" onclick=\"active(this)\">表结构</li>\n");
            sb.append("		    <li id=\"index\" onclick=\"active(this)\">表索引</li>\n");
            sb.append("		    <li id=\"table\" onclick=\"active(this)\">表数据</li>\n");
            sb.append("		</ul>\n");
            sb.append("		<div id=\"contents\" style=\"border:1px solid #ccc;\">\n");
            sb.append("		    <div>\n");
            sb.append("		        <iframe id=\"sqlResult\" src=\"").append(servletPath).append("_preparingExec").append("\" onload=\"changeFrameHeight(this)\" frameborder=\"0\" width=\"100%\">\n");
            sb.append("				</iframe>\n");
            sb.append("		    </div>\n");
            sb.append("		    <div style=\"display:none\">\n");
            sb.append("				<iframe id=\"columnsResult\" src=\"").append(servletPath).append("_preparingTable").append("\" onload=\"changeFrameHeight(this)\" frameborder=\"0\" width=\"100%\">\n");
            sb.append("				</iframe>\n");
            sb.append("		    </div>\n");
            sb.append("		    <div style=\"display:none\">\n");
            sb.append("		        <iframe id=\"indexResult\" src=\"").append(servletPath).append("_preparingTable").append("\" onload=\"changeFrameHeight(this)\" frameborder=\"0\" width=\"100%\">\n");
            sb.append("				</iframe>\n");
            sb.append("		    </div>\n");
            sb.append("		    <div style=\"display:none\">\n");
            sb.append("		        <iframe id=\"tableResult\" src=\"").append(servletPath).append("_preparingTable").append("\" onload=\"changeFrameHeight(this)\" frameborder=\"0\" width=\"100%\">\n");
            sb.append("				</iframe>\n");
            sb.append("		    </div>\n");
            sb.append("		</div>\n");
            sb.append("		<div id=\"shield\"></div>\n");
            sb.append("		<div id=\"toast\">\n");
            sb.append("			<div style=\"margin-left:4px;position:fixed\"><a href=\"javascript:closeShield()\" style=\"font-size: 20px\">✘</a></div>\n");
            sb.append("			<table id=\"toastTable\" style=\"margin:23px\" cellpadding=\"5\" width=\"95%\"></table>\n");
            sb.append("		</div>\n");
            sb.append("	</form>\n");
            sb.append("</body>\n");
            sb.append("</html>");
            return sb.toString();
        } catch (Exception e) {
            String errorMsg = ExceptionUtils.getStackTrace(e);
            logger.error("[xsql] XSqlController.xsql error : " + errorMsg);
            return hintMsg(errorMsg);
        }
    }

    @ResponseBody
    @RequestMapping("/_xsql1")
    public String _xsql1(HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        System.out.println(JSON.toJSONString(parameterMap));
        Thread.sleep(1000);
//        String pageNoStr = request.getParameter("page");
//        int pageNo = StringUtils.isBlank(pageNoStr) ? 1 : Integer.valueOf(pageNoStr);
//        List<Map<String, Object>> mapList = xSqlService.execByPage(sql, pageNo);
        response.setHeader("Access-Control-Allow-Origin", "*");

        return JSON.toJSONString(parameterMap);
    }

    @ResponseBody
    @RequestMapping("/_xsql")
    public String _xsql(HttpServletRequest request, HttpServletResponse response) {
        try {
            String sql = request.getParameter("sql");
            if (StringUtils.isBlank(sql)) {
                throw new IllegalArgumentException("sql不能为空");
            }
            String type = request.getParameter("type");
            if (StringUtils.isBlank(type)) {
                throw new IllegalArgumentException("执行类型不能为空");
            }
            if (StringUtils.equalsIgnoreCase(type, XSqlExecType.EXEC.name())) {
                String pageNoStr = request.getParameter("pageNo");
                int pageNo = StringUtils.isBlank(pageNoStr) ? 1 : Integer.valueOf(pageNoStr);
                List<Map<String, Object>> mapList;
                if (XSqlKeyWord.SELECT.start(sql) || XSqlKeyWord.SHOW.start(sql)) {
                    mapList = xSqlService.execByPage(sql, pageNo);
                } else {
                    mapList = xSqlService.exec(sql);
                }
                String url = request.getRequestURL().toString() + "?type=" + type + "&sql=" + sql;
                return buildHtml(mapList, url);
            } else if (StringUtils.equalsIgnoreCase(type, XSqlExecType.EXPORT.name())) {
                exportSql(sql, response);
                return "";
            } else {
                throw new IllegalArgumentException("未知的执行类型");
            }
        } catch (RuntimeException e) {
            logger.error("[xsql] XSqlController._xsql error : " + e.getMessage());
            return hintMsgWithOnLoad(e.getMessage());
        } catch (Exception e) {
            String errorMsg = ExceptionUtils.getStackTrace(e);
            logger.error("[xsql] XSqlController._xsql error : " + errorMsg);
            return hintMsgWithOnLoad(errorMsg);
        }
    }

    @ResponseBody
    @RequestMapping("/_tableNames")
    public String _tableNames() {
        try {
            List<Object> allTableList = xSqlService.getAllTables();
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<meta charset=\"utf-8\"/>\n");
            sb.append("<style type=\"text/css\">\n");
            sb.append(" * {\n");
            sb.append("		font-size: 15px;\n");
            sb.append("	};\n");
            sb.append("</style>\n");
            sb.append("<script type=\"text/javascript\">\n");
            sb.append("	window.onload = function() {\n");
            sb.append("		var CodeMirror = window.parent.CodeMirror;\n");
            sb.append("		CodeMirror.commands.autocomplete = function(cm) {\n");
            sb.append("	        CodeMirror.showHint(cm, CodeMirror.hint.sql, {\n");
            sb.append("			    tables: {\n");
            for (Object table :allTableList) {
                sb.append("\"").append(table).append("\":[],");
            }
            if (!CollectionUtils.isEmpty(allTableList)) {
                sb.delete(sb.length() - 1, sb.length());
            }
            sb.append("			    }\n");
            sb.append("		    });\n");
            sb.append("     }\n");
            sb.append("	};\n");
            sb.append("	var selectedTable;\n");
            sb.append("	function change(table) {\n");
            sb.append("		if (selectedTable !== table) {\n");
            sb.append("			table.style.color = 'red';\n");
            sb.append("		}\n");
            sb.append("	}\n");
            sb.append("	function restore(table) {\n");
            sb.append("		if (selectedTable !== table) {\n");
            sb.append("			table.style.color = '';\n");
            sb.append("		}\n");
            sb.append("	}\n");
            sb.append("	function choose(table) {\n");
            sb.append("		if (selectedTable) {\n");
            sb.append("			selectedTable.style.color = '';\n");
            sb.append("		}\n");
            sb.append("		table.style.color = 'red';\n");
            sb.append("		selectedTable = table;\n");
            sb.append("	}\n");
            sb.append("</script>\n");
            sb.append("<body>\n");
            sb.append(" <table style=\"font-weight:bold\">\n");
            for (Object table : allTableList) {
                sb.append("     <tr>\n");
                sb.append("         <td>\n");
                sb.append("             <a href=\"javascript:window.parent.query('").append(table)
                        .append("');\" style=\"text-decoration: none;border-bottom: 1px solid;\" onclick=\"choose(this)\" onmouseover=\"change(this)\" onmouseout=\"restore(this)\">")
                        .append(table).append("</a>");
                sb.append("         </td>\n");
                sb.append("     </tr>\n");
            }
            sb.append(" </table>\n");
            sb.append("<body>\n");
            sb.append("</html>");
            return sb.toString();
        } catch (RuntimeException e) {
            logger.error("[xsql] XSqlController._tableNames error : " + e.getMessage());
            return hintMsg(e.getMessage());
        } catch (Exception e) {
            String errorMsg = ExceptionUtils.getStackTrace(e);
            logger.error("[xsql] XSqlController._tableNames error : " + errorMsg);
            return hintMsg(errorMsg);
        }
    }

    @ResponseBody
    @RequestMapping("/_columns")
    public String _columns(HttpServletRequest request) {
        try {
            String table = request.getParameter("table");
            if (StringUtils.isBlank(table)) {
                throw new IllegalArgumentException("表名不能为空");
            }
            List<Map<String, Object>> columnsMapList = xSqlService.getTableColumns(table);
            return buildHtml(columnsMapList, null);
        } catch (RuntimeException e) {
            logger.error("[xsql] XSqlController._columns error : " + e.getMessage());
            return hintMsg(e.getMessage());
        } catch (Exception e) {
            String errorMsg = ExceptionUtils.getStackTrace(e);
            logger.error("[xsql] XSqlController._columns error : " + errorMsg);
            return hintMsg(errorMsg);
        }
    }

    @ResponseBody
    @RequestMapping("/_index")
    public String _index(HttpServletRequest request) {
        try {
            String table = request.getParameter("table");
            if (StringUtils.isBlank(table)) {
                throw new IllegalArgumentException("表名不能为空");
            }
            List<Map<String, Object>> indexMapList = xSqlService.getTableIndex(table);
            return buildHtml(indexMapList, null);
        } catch (RuntimeException e) {
            logger.error("[xsql] XSqlController._index error : " + e.getMessage());
            return hintMsg(e.getMessage());
        } catch (Exception e) {
            String errorMsg = ExceptionUtils.getStackTrace(e);
            logger.error("[xsql] XSqlController._index error : " + errorMsg);
            return hintMsg(errorMsg);
        }
    }

    @ResponseBody
    @RequestMapping("/_loading")
    public String _loading() {
        return hintMsg("正在加载中...");
    }

    @ResponseBody
    @RequestMapping("/_preparingTable")
    public String _preparingTable() {
        return hintMsg("请点击左上的表进行查询");
    }

    @ResponseBody
    @RequestMapping("/_preparingExec")
    public String _preparingExec() {
        return hintMsg("一次只可执行1条查询sql，或任意条增删改sql，用';'分隔");
    }

    private String hintMsg(String hintMsg) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n");
        sb.append("<meta charset=\"utf-8\"/>\n");
        sb.append("<body>\n");
        sb.append(" <pre>").append(hintMsg).append("</pre>\n");
        sb.append("<body>\n");
        sb.append("</html>");
        return sb.toString();
    }

    private String hintMsgWithOnLoad(String hintMsg) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n");
        sb.append("<meta charset=\"utf-8\"/>\n");
        sb.append("<script type=\"text/javascript\">\n");
        sb.append("	window.onload = function () {\n");
        sb.append("		window.parent.document.getElementById('execBtn').disabled = false;\n");
        sb.append("	};\n");
        sb.append("</script>\n");
        sb.append("<body>\n");
        sb.append(" <pre>").append(hintMsg).append(" </pre>\n");
        sb.append("<body>\n");
        sb.append("</html>");
        return sb.toString();
    }

    private String buildHtml(List<Map<String, Object>> mapList, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n");
        sb.append("<meta charset=\"utf-8\"/>\n");
        sb.append("<style type=\"text/css\">\n");
        sb.append("	* {\n");
        sb.append("		font-size: 15px;\n");
        sb.append("	}\n");
        sb.append("	th {\n");
        sb.append("		border:solid#000 1px;\n");
        sb.append("		background-color: lightgrey;\n");
        sb.append("	}\n");
        sb.append("	td {\n");
        sb.append("		border:solid#000 1px;\n");
        sb.append("	}\n");
        sb.append("	table {\n");
        sb.append("		border-collapse:collapse;\n");
        sb.append("		border:none;\n");
        sb.append("		white-space:nowrap;\n");
        sb.append("	}\n");
        sb.append("</style>\n");
        sb.append("<script type=\"text/javascript\">\n");
        sb.append("	window.onload = function () {\n");
        sb.append("		window.parent.document.getElementById('execBtn').disabled = false;\n");
        sb.append("	};\n");
        sb.append("	var selectedTr;\n");
        sb.append("	function change(tr) {\n");
        sb.append("		if (selectedTr !== tr) {\n");
        sb.append("			tr.style.backgroundColor = '#F4F4F4';\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	function restore(tr) {\n");
        sb.append("		if (selectedTr !== tr) {\n");
        sb.append("			tr.style.backgroundColor = '';\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	function choose(tr) {\n");
        sb.append("		if (selectedTr) {\n");
        sb.append("			selectedTr.style.backgroundColor = '';\n");
        sb.append("		}\n");
        sb.append("		tr.style.backgroundColor = '#F4F4F4';\n");
        sb.append("		selectedTr = tr;\n");
        sb.append("	}\n");
        sb.append("</script>\n");
        sb.append("<head>\n");
        sb.append("	<title></title>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        if (CollectionUtils.isEmpty(mapList)) {
            sb.append("	<table align=\"center\" cellpadding=\"5\" width=\"100%;\">\n");
            sb.append("		<tr align=\"center\"><td style=\"border:none; align:center\">无数据</td></tr>\n");
            sb.append("	</table>\n");
        } else {
            sb.append("	<table align=\"center\" cellpadding=\"5\" width=\"100%;\">\n");
            sb.append("		<tr>\n");
            for (String key : mapList.get(0).keySet()) {
                sb.append("		<th>").append(key).append("</th>\n");
            }
            sb.append("		</tr>\n");
            for (Map<String, Object> map : mapList) {
                sb.append("	<tr title=\"双击列块显示\" onmouseover=\"change(this)\" onmouseout=\"restore(this)\"\n");
                sb.append("		ondblclick=\"window.parent.transport(this)\" onclick=\"choose(this)\" style=\"cursor:pointer\" align=\"center\">\n");
                for (String key : map.keySet()) {
                    sb.append("	<td col=\"").append(key).append("\">").append(map.get(key)).append("</td>\n");
                }
                sb.append("	</tr>\n");
            }
            sb.append("	</table>\n");
            if (StringUtils.isNotBlank(url)) {
                sb.append(xSqlService.getPageHtml(url));
            }
        }
        sb.append("</body>\n");
        sb.append("</html>\n");
        return sb.toString();
    }

    private void exportSql(String sql, HttpServletResponse response) throws IOException {
        // 导出的数据
        String exportData = xSqlService.export(sql);
        response.addHeader("Content-Disposition", "attachment;filename=xsql.txt");
        response.getOutputStream().write(exportData.getBytes());
        response.getOutputStream().flush();
    }

}
