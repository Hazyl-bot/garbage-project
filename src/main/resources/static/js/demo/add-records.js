$(document).ready(function() {
    updateTypes();
});

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