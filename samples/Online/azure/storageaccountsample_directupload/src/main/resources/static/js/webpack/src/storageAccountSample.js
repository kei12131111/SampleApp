const { BlobServiceClient } = require("@azure/storage-blob");

const listButton = document.getElementById("list-button");
const fileList = document.getElementById("file-list");
const fileInput = document.getElementById("file");
const uploadButton = document.getElementById("upload-file");
const downloadButton = document.getElementById("download-file");
const status = document.getElementById("status");
const fileContent = document.getElementById("file-content");

// Status表示用
const reportStatus = message => {
    status.innerHTML += `${message}<br/>`;
    status.scrollTop = status.scrollHeight;
}

// ファイルの中身表示用
const reportFile = message => {
    fileContent.innerHTML += `${message}<br/>`;
    fileContent.scrollTop = fileContent.scrollHeight;
}

// ファイルアップロード
uploadButton.addEventListener('click', function(){
const upload = async (containerClient) => {
    try {
        reportStatus("Uploading files...");
        const promises = [];
        for (const file of fileInput.files) {
            const blockBlobClient = containerClient.getBlockBlobClient(file.name);
            promises.push(blockBlobClient.uploadData(file));
        }
        await Promise.all(promises);
        reportStatus("Done.");
    }
    catch (error) {
            reportStatus(error.message);
    }
}

getAjax("upload")
.then(upload)
.catch(function(jqXHR, textStatus, errorThrown, getresult) {
    reportStatus("Error");
});
return false;
});

// ファイルダウンロード
downloadButton.addEventListener("click", function(){
const download = async (containerClient) => {
    try {
        reportStatus("Downloading files...");
        fileContent.innerHTML = ``;
        const fileValue = fileList.value;
        const blobClient = containerClient.getBlobClient(fileValue);
        const downloadBlockBlobResponse = await blobClient.download();
        const downloaded = await blobToString(await downloadBlockBlobResponse.blobBody);
        reportFile(`<b>${fileValue}:</b><br/>${downloaded}`)
        reportStatus("Done.");

        // Blobの中身をStringに変換する
        async function blobToString(blob) {
            const fileReader = new FileReader();
            return new Promise((resolve, reject) => {
                fileReader.onloadend = (ev) => {
                    resolve(ev.target.result);
                };
                fileReader.onerror = reject;
                fileReader.readAsText(blob);
            });
        }
    }
    catch (error) {
            reportStatus(error.message);
    }
}
    getAjax("download")
.then(download)
.catch(function(jqXHR, textStatus, errorThrown, getresult) {
    reportStatus("Error");
});
return false;
});


// ファイルリスト化
listButton.addEventListener("click", function(){
const listFiles = async (containerClient) => {
    fileList.size = 0;
    fileList.innerHTML = "";
    try {
        reportStatus("Retrieving file list...");
        let iter = containerClient.listBlobsFlat();
        let blobItem = await iter.next();
        while (!blobItem.done) {
            fileList.size += 1;
            fileList.innerHTML += `<option value="${blobItem.value.name}">${blobItem.value.name}</option>`;
            blobItem = await iter.next();
        }
        if (fileList.size > 0) {
            reportStatus("Done.");
        } else {
            reportStatus("The container does not contain any files.");
        }
    } catch (error) {
        reportStatus(error.message);
    }
}
getAjax("list")
.then(listFiles)
.catch(function(jqXHR, textStatus, errorThrown, getresult) {
    reportStatus("Error");
});
return false;
});

const getAjax = function (method) {
    const dfd = new $.Deferred();

    $.ajax({ // (2)
        url: 'http://localhost:8080/' + method + '?info',
        type: 'GET'
    }).then(function(data) {
        const blobServiceClient = new BlobServiceClient(data.targetUrl);
        const containerClient = blobServiceClient.getContainerClient(data.containerName);
        dfd.resolve(containerClient);
        
    }).catch(function(jqXHR, textStatus, errorThrown) {
        reportStatus("Error Get SAS");
    });
    return dfd.promise();
}