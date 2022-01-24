$(document).ready(function() {
    if ($('body').is('.reservationBody')) {
        document.getElementById("selectReservationItemModel").addEventListener('change', function (){
            updateReservationPages();
            updateReservations(false)
        });
        document.getElementById("checkBoxActive").addEventListener('change', function (){
            updateReservationPages();
            updateReservations(false)
        });
        document.getElementById("selectPage").addEventListener("change", function() {
            updateReservations(true);
        });
        updateReservationPages();
        updateReservations(false);
    }
});

function updateReservations(checkSelectedPage){
    let actionUrl = "/ajax/filterReservations/"
    let itemModel = document.getElementById("selectReservationItemModel").value;
    let active = document.getElementById("checkBoxActive").checked;
    actionUrl += itemModel+"/"+getSelectedPage(checkSelectedPage);

    ajaxCallGetReservations(actionUrl, {active: active});
}

function updateReservationPages(){
    let itemModel = document.getElementById("selectReservationItemModel").value;
    let actionUrl = "/ajax/filterReservationsGetPages/" + itemModel;
    let active = document.getElementById("checkBoxActive").checked;

    let loadingHTML = '<div class="align-self-center spinner-border" role="status">\n' +
        '  <span class="sr-only">Loading...</span>\n' +
        '</div>';
    $('#resultBlock').html(loadingHTML);

    ajaxCallGetPages(actionUrl, {active: active})
}

function ajaxCallGetReservations(actionUrl, data){
    $.ajax({
        method: 'GET',
        url: actionUrl,
        data: data,

        success: function (result) {
            let innerHTML="";
            let reservations = JSON.parse(result);

            for (let reservation of reservations){
                let today = new Date();
                let cancelHTML ='';
                if(reservation.initDate > today.getTime()){
                    cancelHTML = '<div class="card-bottom d-flex justify-content-center m-2"> ' +
                        '    <form action="/reservations/delete?id='+reservation.reservationId+'/edit" method="post"> ' +
                        '        <button type="submit">Cancel Reservation</button> ' +
                        '    </form> ' +
                        '</div> ';
                }

                let reservationHTML =
                    '     <div class="reservationCard card col mb-4 shadow bg-light p-0" style="max-width: 540px;"> ' +
                    '         <a href="/reservations/' + reservation.reservationId + '/view" class="text-decoration-none text-dark"> ' +
                    '             <div class="row no-gutters m-2"> ' +
                    '                 <div class="col-md-4 "> ' +
                    '                     <h5 class="card-title mb-2">ID: ' + reservation.reservationId+ '</h5> ' +
                    '                     <img src="reservation.item.itemModel.img" class="card-img" alt="reservation image"> ' +
                    '                 </div> ' +
                    '                 <div class="col-md-8 "> ' +
                    '                     <div class="card-body"> ' +
                    '                         <p class="card-text">'+reservation.item.itemModel.name+'</p> ' +
                    '                         <p class="card-text">'+convertDate(reservation.initDate)+'</p> ' +
                    '                         <p class="card-text">'+convertDate(reservation.endDate)+'</p> ' +
                    '                     </div> ' +
                    '                  </div> ' +
                    '              </div> ' +
                    '          </a> ' +
                    cancelHTML+
                    '      </div> ';
                innerHTML += reservationHTML;
            }
            $('#resultBlock').html(innerHTML);
        }
    });

    function convertDate(inputFormat) {
        function pad(s) { return (s < 10) ? '0' + s : s; }
        let d = new Date(inputFormat)
        return [pad(d.getDate()), pad(d.getMonth()+1), d.getFullYear()].join('/')
    }
}