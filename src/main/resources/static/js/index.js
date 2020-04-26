$(function(){
    $("input").click(function(){
        $("#resCheck").hide();
    });

    $("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	//ajax异步传输消息，不同于登陆注册直接用name标签获取消息
	var title = $("#post-title").val();
	var content = $("#post-content").val();
	//发送异步请求
	$.post(
	    //发送给服务器的路径和内容
	     CONTENT_PATH+"/discuss/add",
	    {"title":title,"content":content},
	    //回调函数，获取服务器返回内容
	    function(data){
	        data = $.parseJSON(data);
	        //在提示框中显示返回消息,hintBody为提示框ID
	        $("#hintBody").text(data.msg);
	        //显示提示框
            $("#hintModal").modal("show");
            //2秒后，自动隐藏提示框
            setTimeout(function(){
                $("#hintModal").modal("hide");
                //刷新页面
                if(data.code == 0){
                    window.location.reload();
                }
            }, 2000);
	    }
	);

}