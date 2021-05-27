window.onload = function() {
    document.getElementById("createData").addEventListener("click", createFile);
    let labellist = document.getElementsByTagName("label");
    for (let i = 0; i < labellist.length; i++) {
        let tmp = labellist[i].htmlFor;
        document.getElementById(tmp).classList.remove("is-invalid");
        document.getElementById(tmp).addEventListener("keydown", (evt) => { evt.target.classList.remove("is-invalid") });
    }
}

const createFile = (evt) => {
    if (checkForm()) {
        let trainingData = [];
        let labels = document.getElementsByTagName("label");
        for (let i = 0; i < labels.length; i++) {
            let tmpTxt = labels[i].innerHTML;
            let tmpFor = labels[i].htmlFor;
            tmpObj = {};
            tmpObj.label = tmpTxt;
            tmpObj.data = document.getElementById(tmpFor).value;
            trainingData.push(tmpObj);
        }
        let trainingDataJson = Base64.encode(JSON.stringify(trainingData));
        window.open("http://training.mkservices.de/anschreiben/index.php?daten=" + trainingDataJson);
    }
}

const checkForm = () => {
    let labels = document.getElementsByTagName("label");
    let confirmed = true;
    for (let i = 0; i < labels.length; i++) {
        let tmpId = labels[i].htmlFor;
        let elem = document.getElementById(tmpId);
        let value = elem.value;
        if (value.trim() == "") {
            confirmed = false;
            document.getElementById(tmpId).classList.add("is-invalid");
        }
    }
    return confirmed;
}