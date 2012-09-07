<%
	config.require("patient")
%>

<div class="panel" id="${ config.id }">
	<span class="title"></span>
</div>

<script type="text/javascript">
	var ${ config.id }_person = ${ personJson };

	jq(function() {
		jq('#${ config.id } .title').html(emr.formatPreferredName(${ config.id }_person));
	});
</script>
