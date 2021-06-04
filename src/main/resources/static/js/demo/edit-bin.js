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