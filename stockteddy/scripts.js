const api_handler = "http://127.0.0.1:5000";

function symbolsearch(form) {
    window.event.preventDefault();
    var symbol = form.symbol.value;
    
    getProfile(symbol);
    getQuote(symbol);
    getTrend(symbol);
    getCandle(symbol);
    getNews(symbol);
}

function getProfile(symbol) {
    fetch(api_handler+"/profile?symbol="+symbol).then(
        function(response) {
            if(response.status != 200) {
                console.log("Something went worng..." + response.status);
                return;
            }
            
            response.json().then(
                function(data) {
                    document.getElementById("company-logo").src = data.logo;
                    document.getElementById("company-name").innerText = data.name;
                    var tickerEle = document.getElementsByClassName("stock-ticker-symbol");
                    for(var i = 0; i < tickerEle.length; i++) {
                        tickerEle[i].innerText = symbol;
                    }
                    document.getElementById("stock-exchange-code").innerText = data.exchange;
                    document.getElementById("company-start-date").innerText = data.ipo;
                    document.getElementById("category").innerText = data.finnhubIndustry;

                    showTab();
                }
            )
        }
    ).catch(
        function(err) {
            console.log("Fetch error: -S", err);
        }
    )
}

function getQuote(symbol) {
    fetch(api_handler+"/quote?symbol="+symbol).then(
        function(response) {
            if(response.status != 200) {
                console.log("Something went worng..." + response.status);
                return;
            }
            
            response.json().then(
                function(data) {
                    var date = formattedDate(new Date());
                    document.getElementById("trading-day").innerText = date;
                    document.getElementById("prev-closing-price").innerText = data.pc;
                    document.getElementById("opening-price").innerText = data.o;
                    document.getElementById("high-price").innerText = data.h;
                    document.getElementById("low-price").innerText = data.l;
                    document.getElementById("change").innerHTML = data.d + arrow(data.d);
                    document.getElementById("change-percent").innerHTML = data.dp + arrow(data.dp);
                }
            )
        }
    ).catch(
        function(err) {
            console.log("Fetch error: -S", err);
        }
    )
}

function arrow(data) {
    if(data < 0) {
        return " <img src=\"../images/RedArrowDown.png\" width=\"15px\" height=\"15px\">"; 
    }
    return " <img src=\"../images/GreenArrowUp.png\" width=\"15px\" height=\"15px\">"; 
}

function getTrend(symbol) {
    fetch(api_handler+"/trend?symbol="+symbol).then(
        function(response) {
            if(response.status != 200) {
                console.log("Something went worng..." + response.status);
                return;
            }
            
            response.json().then(
                function(data) {
                    document.getElementById("ss").src = data[0].strongSell;
                    document.getElementById("s").innerText = data[0].sell;
                    document.getElementById("h").innerText = data[0].hold;
                    document.getElementById("b").innerText = data[0].buy;
                    document.getElementById("sb").innerText = data[0].strongBuy;
                }
            )
        }
    ).catch(
        function(err) {
            console.log("Fetch error: -S", err);
        }
    )
}

function getCandle(symbol) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET',api_handler + '/candle?symbol='+ symbol, true);
    xhr.onload = function() {
        document.getElementById("charts").innerText = this.response;
    }
    xhr.send(null);
}

function getNews(symbol) {
    fetch(api_handler+"/news?symbol="+symbol).then(
        function(response) {
            if(response.status!=200) {
                console.log("Something went wrong..." + response.status);
                return;
            }

            response.json().then(
                function(data) {
                    var newsBlocks = document.getElementsByClassName("news-block");
                    for(var i = 0; i < newsBlocks.length; i++) {
                        var img = "<img src=\""+data[i].image+"\" width=\"100px\" height=\"100px\">";
                        var date = formattedDate(new Date(data[i].date));
                        var div = "<div><span class=\"bold\">"+data[i].headline+"</span><br>"+date+"<br><a href=\""+data[i].url+"\">See Original Post</a></div>";
                        newsBlocks[i].innerHTML = img+div;
                    } 
                }
            )
        }
    ).catch(
        function(err) {
            console.log("Fetch error: -S", err);
        }
    )
}

function clearInput() {
    document.getElementById("symbol").value = "";
}

function showTab() {
    if(document.getElementById("tab-container").style.display != "inline-block") {
        document.getElementById("tab-container").style.display = "inline-block";
        document.getElementById("company-btn").className += " active";
        document.getElementById("company").style.display = "block";
    }
}

function openTab(btn, tabName) {
    var tabContents = document.getElementsByClassName("tabcontent");
    for(var i = 0; i < tabContents.length; i++) {
        tabContents[i].style.display = "none";
    }

    var tabLinks = document.getElementsByClassName("tablinks");
    for(var i=0; i < tabLinks.length; i++) {
        tabLinks[i].className = tabLinks[i].className.replace(" active","");
    }

    document.getElementById(tabName).style.display = "block";
    btn.className += " active";
}

function formattedDate(date) {
    return date.toLocaleDateString("en-GB",{day:'numeric', month:'long', year:'numeric' });
}