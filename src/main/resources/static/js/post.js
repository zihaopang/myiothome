$(function(){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDeleteBtn);
});

function like(btn,entityType,entityId,postUserId){
    $.post(
        CONTENT_PATH+"/like",
        {"entityType":entityType,"entityId":entityId,"postUserId":postUserId},
        function(data){
            data = $.parseJSON(data);
            if(data.code == 0){
                $(btn).children("i").text(data.likeNum);
                $(btn).children("b").text(data.likeStatus==1?'已赞':'赞');
            }else{
                alert(data.msg);
            }
        }
    );
}

function setTop(){
    $.post(
        "/myiothome/discuss/top",
        {"postId":$("#postId").val()},
        function(data){
            data = $.parseJSON(data);
            if(data.code == 0){
                $("#topBtn").attr("disabled","disabled");
            }else{
                alert(data.msg);
            }
        }
    );
}

function setWonderful(){
    $.post(
        "/myiothome/discuss/wonderful",
        {"postId":$("#postId").val()},
        function(data){
            data = $.parseJSON(data);
            if(data.code == 0){
                $("#wonderfulBtn").attr("disabled","disabled");
            }else{
                alert(data.msg);
            }
        }
    );
}

function setDeleteBtn(){
    $.post(
        "/myiothome/discuss/delete",
        {"postId":$("#postId").val()},
        function(data){
            data = $.parseJSON(data);
            if(data.code == 0){
                location.href="/myiothome/index";
            }else{
                alert(data.msg);
            }
        }
    );
}