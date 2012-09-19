<!-- This is the login page -->
<%
	ui.decorateWith("emr", "standardEmrPage")
%>

<form method="post" action="/${ contextPath }/loginServlet" autocomplete="off">
	<table>
		<tr>
			<th>Username</th>
			<td><input id="uname" type="text" name="uname"/></td>
		</tr>
		<tr>
			<th>Password</th>
			<td><input type="password" name="pw"/></td>
		</tr>
		<tr>
			<th></th>
			<td>
				<input type="submit" value="Login"/>
			</td>
		</tr>
	</table>
</form>

<script type="text/javascript">
	document.getElementById('uname').focus();
</script>
