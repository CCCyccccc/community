$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	// 隐藏弹出框
	$("#publishModal").modal("hide");
	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送异步请求(POST)
	$.post(
	    CONTEXT_PATH + "/discuss/add",
	    {"title":title, "content":content},
	    // 回调函数，处理服务器返回的值
	    function(data) {
	        // data转义JSON格式
	        data = $.parseJSON(data);
	        // 在提示框中显示返回消息
	        $("#hintBody").text(data.msg);
	        // 显示提示框
            $("#hintModal").modal("show");
            // 2秒后自动隐藏
            setTimeout(function(){
            	$("#hintModal").modal("hide");
            	// 刷新页面
            	if(data.code == 0){
            	    window.location.reload();
            	}
            }, 2000);
	    }
	);
}