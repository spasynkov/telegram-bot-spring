window.onload = function () {
    let location = window.location.href;
    console.log(location);
    if (location.toString().includes('/edit.html')) {
        editPage();
    } else if (location.toString().includes('/list.html')) {
        printList();
    } else if (location.toString().includes('/delete.html')) {
        deleteSong();
    }
};

function printList() {
    let listContainer = document.getElementById('list-container');
    var request = new XMLHttpRequest();
    request.open('GET', '/rest/all', true);

    request.onload = function () {
        var data = JSON.parse(this.response);

        listContainer.innerText = '';
        let array = [];
        if (request.status >= 200 && request.status < 400) {
            data.forEach(x => {
                let a = x.artist;
                let s = x.song;
                let o = {a, s};
                array.push(o);
            })
        } else {
            console.log('error')
        }

        const unique = array.unique();
        if (unique.length === 0) {
            listContainer.innerText = 'No songs found';
        }
        unique.forEach(x => {
            listContainer.innerHTML += '<li><a class="link">' + x.a + ' - ' + x.s + '</a>' +
                '<a href="edit.html?artist=' + x.a + '&song=' + x.s + '" class="edit"><img src="img/edit-icon.png"></a>' +
                '<a href="delete.html?artist=' + x.a + '&song=' + x.s + '" class="delete"><img src="img/delete-icon.png"></a></li>';
        })
    };
    request.send();
}

function addSong() {
    let form = document.getElementById("editSongForm");

    let artist = document.getElementsByName("artist")[0].value;
    let song = document.getElementsByName("song")[0].value;
    let text = document.getElementsByName("text")[0].value;

    let couplets = text.split('\n\n');
    //console.log(couplets);
    for (let coupletId = 1; coupletId <= couplets.length; coupletId++) {
        let couplet = couplets[coupletId - 1];
        //console.log(couplet);
        if (couplet === '') continue;

        let strings = couplet.split('\n');
        let text = [];
        for (let i = 0; i < strings.length; i++) {
            text.push(strings[i]);
        }
        let json = {artist: artist, song: song, coupletId: coupletId, text: text};

        console.log(json);
        var xhr = new XMLHttpRequest();
        var url = '/rest/all/artist/' + artist + '/song/' + song;
        xhr.open("PUT", url, true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var json = JSON.parse(xhr.responseText);
                console.log(json);
            }
        };
        var data = JSON.stringify(json);
        xhr.send(data);
    }

    /*form.removeEventListener("submit", function preventDef(event) {
        event.preventDefault();
    }, false);
    window.location.href = '/rest/all/artist/' + artist + '/song/' + song;*/
}

function editPage() {
    let form = document.getElementById("editSongForm");

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);

    var request = new XMLHttpRequest();
    request.open('GET', '/rest/all/artist/' + decodeURI(urlParams.get('artist'))
        + '/song/' + decodeURI(urlParams.get('song')), true);

    request.onload = function () {
        var data = eval(JSON.parse(this.response));
        console.log(data);
        data.sort(function (a, b) {
            return a.coupletId - b.coupletId;
        });

        data.forEach(d => {
            document.getElementsByName("artist")[0].value = d.artist;
            document.getElementsByName("song")[0].value = d.song;
            d.text.forEach(s => {
                document.getElementsByName("text")[0].value += s + '\n';
            });
            document.getElementsByName("text")[0].value += '\n';
        });
        document.getElementsByName("text")[0].value = document.getElementsByName("text")[0].value.trim();
    };
    request.send();
}

function updateSong() {
    let artist = document.getElementsByName("artist")[0].value;
    let song = document.getElementsByName("song")[0].value;
    let text = document.getElementsByName("text")[0].value;

    let couplets = text.split('\n\n');
    //console.log(couplets);
    for (let coupletId = 0; coupletId < couplets.length; coupletId++) {
        let couplet = couplets[coupletId];
        //console.log(couplet);
        if (couplet === '') continue;

        let strings = couplet.split('\n');
        let text = [];
        for (let i = 0; i < strings.length; i++) {
            text.push(strings[i]);
        }
        let json = {artist: artist, song: song, coupletId: (coupletId + 1), text: text};

        var xhr = new XMLHttpRequest();
        var url = '/rest/all/artist/' + artist + '/song/' + song;
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var json = JSON.parse(xhr.responseText);
                console.log(json);
            }
        };
        var data = JSON.stringify(json);
        xhr.send(data);
    }
}

function deleteSong() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);

    let artist = decodeURI(urlParams.get('artist'));
    let song = decodeURI(urlParams.get('song'));

    if (confirm('Are you sure you want to delete "' + artist + ' - ' + song + '"?')) {
        var request = new XMLHttpRequest();
        request.open('GET', '/rest/all/artist/' + artist + '/song/' + song, true);

        request.onload = function () {
            var resp = eval(JSON.parse(this.response));

            resp.forEach(r => {
                var xhr = new XMLHttpRequest();
                var url = '/rest/all/artist/' + artist + '/song/' + song;
                xhr.open("DELETE", url, true);
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        var json = JSON.parse(xhr.responseText);
                        console.log(json);
                    }
                };
                var data = JSON.stringify(r);
                xhr.send(data);
            });

            document.getElementsByTagName("body")[0].innerText = '"' + artist + ' - ' + song + '" deleted';
        };
        request.send();
    } else {
        document.getElementsByTagName("body")[0].innerText = '"' + artist + ' - ' + song + '" was not deleted (canceled)';
    }
}