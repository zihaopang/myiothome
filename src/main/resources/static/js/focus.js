$(function(){
    $("#focusBtn").click(focus);
});

function focus(){
    var btn = this;
    if($(btn).hasClass("btn-primary")){
        $.post(
            CONTENT_PATH+"/focus",
            {"entityType":3,"entityId":$(btn).prev().val()},
            function(data){
                data = $.parseJSON(data);
                if(data.code==0){
                  $(btn).text("取消关注").removeClass("btn-primary").addClass("btn-danger");
                  $("#fansNum").text(data.fansNum);
                }else{
                    alert(data.msg);
                }
            }
        );
    }else{
        $.post(
             CONTENT_PATH+"/unfocus",
            {"entityType":3,"entityId":$(btn).prev().val()},
            function(data){
                data = $.parseJSON(data);
                if(data.code==0){
                    $(btn).text("关注TA").removeClass("btn-danger").addClass("btn-primary");
                    $("#fansNum").text(data.fansNum);
                }else{
                    alert(data.msg);
                }
            }
        );
    }
}