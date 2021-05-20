// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

// Pie Chart Example
const ctxPie = document.getElementById("myPieChart");
const myPieChart = new Chart(ctxPie, {
  type: 'doughnut',
  data: {
    labels: ["可回收垃圾", "厨余垃圾", "干垃圾", "湿垃圾", "有害垃圾", "其他垃圾"],
    datasets: [{
      data: typeData,
      backgroundColor: ['#4e73df', '#f6c23e', '#36b9cc', '#1cc88a', '#e74a3b', '#858796'],
      hoverBackgroundColor: ['#2e59d9', '#dba00a', '#2ea6b7', '#169d6c', '#cc2919', '#5a5c69'],
      hoverBorderColor: "rgba(234, 236, 244, 1)",
    }],
  },
  options: {
    maintainAspectRatio: false,
    tooltips: {
      backgroundColor: "rgb(255,255,255)",
      bodyFontColor: "#858796",
      borderColor: '#dddfeb',
      borderWidth: 1,
      xPadding: 15,
      yPadding: 15,
      displayColors: false,
      caretPadding: 10,
    },
    legend: {
      display: false
    },
    cutoutPercentage: 80,
  },
});
