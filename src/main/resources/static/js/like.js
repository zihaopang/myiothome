//function like(btn,entityType,entityId,postUserId){
//    $.post(
//        "/myiothome/like",
//        {"entityType":entityType,"entityId":entityId,"postUserId":postUserId},
//        function(data){
//            data = $.parseJSON(data);
//            if(data.code == 0){
//                $(btn).children("i").text(data.likeNum);
//                $(btn).children("b").text(data.likeStatus==1?'已赞':'赞');
//            }else{
//                alert(data.msg);
//            }
//        }
//    );
//}