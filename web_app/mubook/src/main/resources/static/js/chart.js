window.onload = function loadChart(){
    var labels = /*[[${key}]]*/ [];
    var data = /*[[${value}]]*/ [];
    var name = '[[${name}]]';
    var chartType = '[[${type}]]';
    
        var data = {
            labels: labels,
            datasets: [{
                label: name,
                backgroundColor: 'rgb(255, 99, 132)',
                borderColor: 'rgb(255, 99, 132)',
                data: data,
            }]
        };
        
        var config = {
        type: chartType,
        data: data,
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
        };
    
        var myChart = new Chart(
            document.getElementById('chart-can'),
            config
        );
}