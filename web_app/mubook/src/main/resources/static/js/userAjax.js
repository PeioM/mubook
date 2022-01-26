$(document).ready(function() {
    if ($('body').is('.userBody')) {
        document.getElementById("searchUserButton").addEventListener('click', function (){
            updateUserPages();
            updateUsers(false)
        });
        document.getElementById("selectUserType").addEventListener('change', function (){
            updateUserPages();
            updateUsers(false)
        });
        document.getElementById("selectPage").addEventListener("change", function() {
            updateUsers(true);
        });
        updateUserPages();
        updateUsers(false);
    }
});

function updateUsers(checkSelectedPage){
    let actionUrl = "/ajax/filterUsers/"
    let containStr = document.getElementById("searchTextBox").value;
    let userTypeId = document.getElementById("selectUserType").value;
    actionUrl += userTypeId+"/"+getSelectedPage(checkSelectedPage);

    toggleFilters(document.getElementsByClassName("filterOption"));
    loadingSearch();

    ajaxCallGetUsers(actionUrl, containStr);
}

function updateUserPages(){
    let containStr = document.getElementById("searchTextBox").value;
    let userTypeId = document.getElementById("selectUserType").value;
    let actionUrl = "/ajax/filterUsersGetPages/" + userTypeId;

    ajaxCallGetPages(actionUrl, {containStr: containStr})
}

function ajaxCallGetUsers(actionUrl, containStr){
    $.ajax({
        method: 'GET',
        url: actionUrl,
        data:{containStr: containStr},

        success: function (result) {
            let innerHTML="";
            let users = JSON.parse(result);

            for (let user of users){
                let usersHTML =
                    '<div class="card col mb-4 shadow bg-light p-0"> ' +
                    '    <a href="/user/'+user.userId+'/edit" ' +
                    '           class="userCardLink text-decoration-none text-dark trackGrafana" grafanaId="18"> ' +
                    '       <img class="card-img-top img-fluid" src="'+user.profileImg+'" alt="User '+user.userId+' profile image">' +
                    '       <div class="card-body text-center"> ' +
                    '           <h5 class="card-title">ID: '+user.userId+'</h5> ' +
                    '           <div class="p-1"> ' +
                    '               <p class="m-0">Name: '+user.name+'</p> ' +
                    '               <p class="m-0">Surname: '+user.surname+'</p> ' +
                    '               <p class="m-0">Username: '+user.username+'</p> ' +
                    '               <p class="m-0">Email: '+user.email+'</p> ' +
                    '           </div> ' +
                    '       </div> ' +
                    '     </a> ' +
                    '</div>';
                innerHTML += usersHTML;
            }
            $('#resultBlock').html(innerHTML);
            toggleFilters(document.getElementsByClassName("filterOption"));

            let itemModelCards = document.getElementsByClassName("userCardLink");
            for(let card of itemModelCards){
                card.addEventListener("click", function(){
                    ajaxCallRegisterButtonClick(card.getAttribute("grafanaId"));
                });
            }
        }
    });
}