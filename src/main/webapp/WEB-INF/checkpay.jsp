<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@ page import = "java.io.*, java.net.*, java.util.*, org.json.simple.*, org.json.simple.parser.JSONParser" %>
<%
String RTP_KEY="5441a099-4d9f-401e-a4ee-9c4bf836f76d";	//인증키값 설정

JSONObject jobj = new JSONObject();


if (RTP_KEY.equals(request.getParameter("regPkey"))) {
	String retString=getRTPay(RTP_KEY, jobj, request);

	JSONParser parser = new JSONParser();
	Object obj = parser.parse(retString);
	JSONObject jsonObj = (JSONObject) obj;

	jobj.put("RCODE", jsonObj.get("RCODE"));
	if ("200".equals((String)jobj.get("RCODE"))) {
		String pbank=(String)jsonObj.get("RBANK");	//은행명
		String pname=(String)jsonObj.get("RNAME");	//입금자명
		String pmoney=(String)jsonObj.get("RPAY");	//입금금액
		String tall=(String)jsonObj.get("RTEXT");	//전송 데이터 전문

		
		
		//========================== 인증키값 설정과 이 부분만 고쳐주세요. =======================

		//입금신청을 기록하신 기존 DB 데이터와 비교하는 코드 입력부분
		//입금자명과 금액이 일치하는 갯수를 비교하여 한개 이상이면 입금완료 처리 보류

		//입금데이터와 비교하여 매칭이 되었을 경우 jobj.put("PCHK", "OK"); 
		//입금데이터와 비교하여 매칭이 되지 않았을 경우 jobj.put("PCHK", "NO"); 

		//========================== 인증키값 설정과 이 부분만 고쳐주세요. =======================
		jobj.put("PCHK", "OK");
	} else {
		jobj.put("PCHK", "NO");
	}
} else {
	if ("".equals(request.getParameter("regPkey"))||request.getParameter("regPkey")==null||"null".equals(request.getParameter("regPkey"))) {
		String retString=getRTPay(RTP_KEY, jobj, request);
	}
	jobj.put("PCHK", "NO");
	jobj.put("RCODE", "400");
}

out.print(jobj.toJSONString());
%>


<%!
public String getRTPay(String RTP_KEY, JSONObject jobj, HttpServletRequest request) {
	String ugrd = request.getParameter("ugrd");
	String RTP_URL = "https://rtpay.net/CheckPay/checkpay.php";
	String data = "";
	String name = "";
	String retStr = "";
	if ("11".equals(ugrd)||"12".equals(ugrd)) RTP_URL="https://rtpay.net/CheckPay/test_checkpay.php";

	Enumeration enums = request.getParameterNames();
	while(enums.hasMoreElements()) {
		name = (String)enums.nextElement();
		if (!data.equals("")) data+="&";
		data += name+"="+URLEncoder.encode(request.getParameter(name));
	}

	if ("".equals(data)) {
		data="regPkey="+RTP_KEY+"&Referer="+request.getRequestURL();
		RTP_URL="https://rtpay.net/CheckPay/setPurl.php";
	}

	try {
		URL url = new URL(RTP_URL);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");

		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
        String line;
        while ((line = rd.readLine()) != null) {
		   retStr+=line;
        };
        rd.close();
		wr.close();
	} catch (Exception e) {
		return "{\"RCODE\":\"600\",\"EMSG\":\""+e.getMessage()+"\"}";
	}

	if (!retStr.equals("")) {
		return retStr;
	} else {
		return "{\"RCODE\":\"600\"}";
	}
}
%>