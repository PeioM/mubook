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

    toggleFilters(document.getElementsByClassName("filterOption"));
    loadingSearch();

    ajaxCallGetReservations(actionUrl, {active: active});
}

function updateReservationPages(){
    let itemModel = document.getElementById("selectReservationItemModel").value;
    let actionUrl = "/ajax/filterReservationsGetPages/" + itemModel;
    let active = document.getElementById("checkBoxActive").checked;

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
                if(reservation.initDate + (1000*60*60*24) > today.getTime()){
                    cancelHTML = '<div class="card-bottom d-flex justify-content-center m-2"> ' +
                        '    <form action="/reservations/'+reservation.reservationId+'/delete" method="get"> ' +
                        '        <button type="submit" class="btn btn-secondary trackGrafana" grafanaId="23">Cancel Reservation</button> ' +
                        '    </form> ' +
                        '</div> ';
                }

                let reservationHTML =
                    '     <div class="card col mb-4 shadow bg-light p-0" style="max-width: 540px;"> ' +
                    '         <a href="/reservations/' + reservation.reservationId + '/view" ' +
                    '           class="reservationCardLink text-decoration-none text-dark trackGrafana" grafanaId="22"> ' +
                    '             <div class="row no-gutters m-2"> ' +
                    '                 <div class="col-md-4 "> ' +
                    '                     <h5 class="card-title mb-2">ID: ' + reservation.reservationId+ '</h5> ' +
                    '                     <img src="'+ reservation.item.itemModel.img +'" class="card-img" alt="reservation image"> ' +
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
            toggleFilters(document.getElementsByClassName("filterOption"));

            let itemModelCards = document.getElementsByClassName("reservationCardLink");
            for(let card of itemModelCards){
                card.addEventListener("click", function(){
                    ajaxCallRegisterButtonClick(card.getAttribute("grafanaId"));
                });
            }
        }
    });

    function convertDate(inputFormat) {
        function pad(s) { return (s < 10) ? '0' + s : s; }
        let d = new Date(inputFormat)
        return [pad(d.getDate()), pad(d.getMonth()+1), d.getFullYear()].join('/')
    }
}