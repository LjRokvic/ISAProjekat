function SortBy(a, b) {
    return ('' + a.name).localeCompare(b.name);
}


$(function () {

    var id = getUrlParameter("id");

    $('#error').hide();
    $('#success').hide();


    $.get({
        url: '/api/hotel/' + id + '/roomTypes',
        headers: {"Authorization": "Bearer " + localStorage.getItem('accessToken')},
        success: function (data) {
            var i = 0;
            if (data != null) {
                data = data.sort(SortBy);
                for (var us in data) {
                    fillRoomType(data[us]);
                }


                var idOfHotelPrice = $('#roomType').children(0).val();
                $('select[name="roomTypeSelected"]').val(idOfHotelPrice);
                $.get({
                    url: '/api/hotel/' + id + '/PriceLists',
                    headers: {"Authorization": "Bearer " + localStorage.getItem('accessToken')},
                    success: function (hotelPriceLists) {
                        hotelPriceLists.forEach(function (hotelPrice) {
                            if (hotelPrice.roomType.id == idOfHotelPrice) {
                                $('input[name="price"]').val(hotelPrice.price);
                            }
                        });
                    }
                });


            } else {

            }
        }
    });


    $('select[name="roomTypeSelected"]').on('change', function () {
        $.get({
            url: '/api/hotel/' + id + '/PriceLists',
            headers: {"Authorization": "Bearer " + localStorage.getItem('accessToken')},
            success: function (hotelPriceLists) {
                var idRoomType = $('select[name="roomTypeSelected"]').val();
                hotelPriceLists.forEach(function (hotelPrice) {
                    if (hotelPrice.roomType.id == idRoomType) {
                        $('input[name="price"]').val(hotelPrice.price);
                    }
                });
            }
        });
    });


    $('#toSubmit').on('click', function (e) {
        e.preventDefault();
        var newValue = $('input[name="price"]').val();
        var idField = $('select[name="roomTypeSelected"]').val();
        $.post({
            url: 'api/hotel/editPriceList',
            headers: {"Authorization": "Bearer " + localStorage.getItem('accessToken')},
            contentType: 'application/json',
            data: JSON.stringify(
                {
                    id: idField,
                    price: newValue,
                    hotel:{
                        id:id
                    }
                }),
            success: function (message) {
                $('#success').text('Successfully changed price to ' + newValue ).fadeIn().delay(10000).fadeOut();
            },
            error: function (message) {
                if (message.status == 401) {
                    $('#toSubmit').attr("disabled", "disabled");
                    $('#error').text("Unauthorized access").fadeIn().delay(5000).fadeOut();
                }
                else if(message.status == 500) {
                    $('#error').text("Server error cause bad parameters sent").fadeIn().delay(5000).fadeOut();
                }
            }
        });

    });

});


function fillRoomType(data) {
    $("#roomType").append('<option value= "' + data.id + '">' + data.name + '</option>');
}

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1].replace(/\+/g, ' '));
        }
    }
};
