// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTable').DataTable();
  updateTypes();
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

document.querySelectorAll(".bin-edit-btn").forEach((i)=>{i.addEventListener("click", ()=>{
  const id = i.id;
  const location = i.parentNode.parentNode.children[0].innerHTML;
  const type = i.parentNode.parentNode.children[1].children[0].innerHTML;
  const capacity = i.parentNode.parentNode.children[2].innerHTML;
  const contain = i.parentNode.parentNode.children[3].innerHTML;
  const url = `/garbage/edit?id=${id}&location=${location}&type=${type}&capacity=${capacity}&contain=${contain}`;
  console.log(url);
  window.location = url;
})})

const typeEnum = {"RECYCLABLE":"可回收垃圾", "WASTE":"厨余垃圾", "DRY":"干垃圾", "WET":"湿垃圾", "HARMFUL":"有害垃圾", "OTHER":"其他垃圾"};

$("#location-select").on("change", ()=>{updateTypes();})

const updateTypes = ()=>{
  const v = $("#location-select option:selected").val();
  document.getElementById("type-select").options.length = 0;
  getTypes(v).then((types)=>{
    console.log(types.length);
    let optionList = "";
    types.forEach((item)=>{
      optionList += `<option value="${item}">${typeEnum[item]}</option>`
    });
    const typeSelect = $("#type-select")
    console.log(optionList);
    typeSelect.append(optionList);
  });
}

const getTypes = async (v) =>{
  const response = await fetch(`/getTypes?location=${v}`);
  const  jsonData = await response.json();
  console.log(jsonData);
  return jsonData;
}