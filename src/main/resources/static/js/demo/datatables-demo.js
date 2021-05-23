// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTable').DataTable();
});

document.querySelectorAll(".bin-delete-btn").forEach((i)=>{i.addEventListener("click", async ()=>{
  const id = i.id;
  const response = await fetch(`/garbage/remove?id=${id}`);
  const jsonData = await response.json();
  console.log(jsonData);
  if (jsonData === 200){
    window.location = location;
  } else if (jsonData === 500){
    alert("delete failed");
  }
})})

