  $(document).ready(function(){
  $("#uploadHeaderForm").submit(upload);
  $("input").click(function(){
    $("#checkPic").hide();
    $("#checkPas").hide();
  });
});
/*
function upload(){
    $.ajax({
        url: "http://upload.qiniup.com",
        method:"post",
        processData:false,
        contentType:false,
        data:new FormData($("#uploadHeaderForm")[0]),
        success:function(data){
            if(data && data.code==0){
                //更新头像访问路径
                $.post(
                    "/myiothome/setting/updateHeader",
                    {"fileName":$("input[name='key']").val()},
                    function(data){
                        data = $.parseJSON(data);
                        if(data.code==0){
                            window.location.reload();
                            alert($("input[name='key']").val())
                        }else{
                            alert(data.msg);
                        }
                    }
                );
            }else{
                alert("上传失败！");
            }
        }
    });
    return false;
}
*/
function upload() {
    $.ajax({
        url: "http://upload.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#uploadHeaderForm")[0]),
        success: function(data) {
            if(data && data.code == 0) {
                // 更新头像访问路径
                $.post(
                    CONTENT_PATH+"/setting/updateHeader",
                    {"fileName":$("input[name='key']").val()},
                    function(data) {
                        data = $.parseJSON(data);
                        if(data.code == 0) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            } else {
                alert("上传失败!");
            }
        }
    });
    return false;
}