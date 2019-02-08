onmessage = function (e) {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.status === 200) {
            postMessage(JSON.parse(xhr.responseText));
        }
        else {
            console.log('Request failed. Returned status of ' + xhr.status);
        }
    };

    var delay =  e.data.delay ? "&delay=250" : "";

    var getUrl = "api/greeting";
    
    xhr.open('GET', getUrl + '?from=' + e.data.from + delay);
    xhr.send();
};
