$(document).ready(function () {

    $.get({
        url : 'http://localhost:8080/api/hotel/findAll',
        success : function(data) {
            if (data != null) {
                for ( var i in data) {
                    addArticle(data[i]);
                }
            }
        }
    });

});


function addArticle(hotel) {
    var icon = "assets/img/hotel.png";
    $('#hotelList').append('<div class="col-sm-6 col-md-5 col-lg-4 item">' +
        '<div class="box">' + '<img src="' + icon + '" style="width:80px;height:80px"/>' +
        '<h3 class="name">' +hotel.name +'</h3>' +
        '<p class="description">Address: <span style = "color:black">'+ hotel.address + '</span></p>' +
        '<p class="description">'+ hotel.description +'</p>' +
        '<a class="edit-hotel admin" href="edithotel.html?id=' + hotel.id +'&name='+ hotel.name +'&description='+ hotel.description + '"><img src="/../assets/img/edit.png" style="height:16px;width16px;"></a> ' +
        '<a id="' + hotel.id + '" + class="delete-hotel admin" href="hotels.html"><img src="assets/img/delete.png" style="height:16px;width16px;"></a> '+
        '</div>');
}