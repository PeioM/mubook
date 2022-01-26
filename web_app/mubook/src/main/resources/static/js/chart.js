window.onload = function loadChart(){
    var labels = /*[[${key}]]*/ [];
    var data = /*[[${value}]]*/ [];
    var name = '[[${name}]]';
    name = name.replace(/["']/g, "");
    var chartType = '[[${type}]]';
    chartType = chartType.replace(/["']/g, "");
    
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
            scales: {
                y: {
                    beginAtZero: true
                }
            },
            responsive: true,
            maintainAspectRatio: true
        }
        };
    
        var myChart = new Chart(
            document.getElementById('chart-can'),
            config
        );
}