var stompClient = null;
var callId = null;
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/callcenter/ws');
    stompClient = Stomp.over(socket);
    // var headers = {
    //     Authorization: 'Bearer ec4ba481-a5a0-4940-b10a-f48169e73311'
    // }

    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/call/0000000391', function (greeting) {
            callId=JSON.parse(greeting.body).callId;
            showGreeting(greeting.body+" call");
        });
        stompClient.subscribe('/topic/invite/0000000391', function (greeting) {
            callId=JSON.parse(greeting.body).callId;
            showGreeting(greeting.body+" invite");
        });
        stompClient.subscribe('/topic/transfer/0000000392', function (greeting) {
            callId=JSON.parse(greeting.body).callId;
            showGreeting(greeting.body+" transfer");
        });
        stompClient.subscribe('/topic/bye/0000000391', function (greeting) {
            showGreeting(greeting.body+" bye");
        });
        stompClient.subscribe('/topic/status/0000000391', function (greeting) {
            showGreeting(greeting.body+" status");
        });        stompClient.subscribe('/topic/register/0000000391', function (greeting) {
            showGreeting(greeting.body+" register");
        });
        stompClient.subscribe('/topic/task/1', function (greeting) {
            showGreeting(greeting.body);
        });
        stompClient.subscribe('/topic/task/1/0000000391', function (greeting) {
            showGreeting(greeting.body);
        });
        stompClient.subscribe('/topic/notify/1', function (greeting) {
            showGreeting(greeting.body+" msg");
        });
        stompClient.subscribe('/topic/notify/1/0000000391', function (greeting) {
            showGreeting(greeting.body+" msg");
        });
        stompClient.subscribe('/topic/notify/0000000391', function (greeting) {
            showGreeting(greeting.body+" msg");
        });
        stompClient.subscribe('/topic/callExtStatus/1', function (greeting) {
            showGreeting(greeting.body+" callExtStatus");
        });
        stompClient.subscribe('/topic/callTaskStatus/1', function (greeting) {
            showGreeting(greeting.body+" callTaskStatus");
        });
        stompClient.subscribe('/topic/conferenceStatus/1', function (greeting) {
            showGreeting(greeting.body+" conferenceStatus");
        });
    },
        function errorCallBack (error) {
            // 连接失败时（服务器响应 ERROR 帧）的回调方法
            console.log('连接失败【' + error + '】');
        });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}
function idle() {
    var call = {
        projectCode: "1",
        workType: "20",
        username: "0000000391"
    };
    var jsonText = JSON.stringify(call);
    stompClient.send("/app/idle", {}, jsonText);
}
function busy() {
    var call = {
        projectCode: "1",
        workType: "20",
        username: "0000000391"
    };
    var jsonText = JSON.stringify(call);
    stompClient.send("/app/busy", {}, jsonText);
}
function previewCall() {
    var call = {
        username: "0000000391",
        callTaskCode: "1",
        telNo: "xxx"
    };
    var jsonText = JSON.stringify(call);
    stompClient.send("/app/previewCall", {}, jsonText);
}
function call() {
    var call = {
        username: "0000000391",
        projectCode: "1",
        telNo: "xxx"
    };
    var jsonText = JSON.stringify(call);
    stompClient.send("/app/call", {}, jsonText);
}
function bye() {
    var bye = {
        username: "0000000391",
        callId: callId
    };
    var jsonText = JSON.stringify(bye);
    stompClient.send("/app/bye", {}, jsonText);
}

function eavesdrop() {
    var bye = {
        username: "0000000391",
        listenerExt: "00000078A1"
    };
    var jsonText = JSON.stringify(bye);
    stompClient.send("/app/eavesdrop", {}, jsonText);
}

function stopEavesdrop() {
    var bye = {
        username: "0000000391",
        listenerExt: "00000078A1"
    };
    var jsonText = JSON.stringify(bye);
    stompClient.send("/app/stopEavesdrop", {}, jsonText);
}

function conference() {
    var bye = {
        projectCode: "1",
        listenerExt: "00000078A1"
    };
    var jsonText = JSON.stringify(bye);
    stompClient.send("/app/conference", {}, jsonText);
}

function stopConference() {
    var bye = {
        projectCode: "1",
        listenerExt: "00000078A1"
    };
    var jsonText = JSON.stringify(bye);
    stompClient.send("/app/stopConference", {}, jsonText);
}

function conferenceByUsername() {
    var bye = {
        username: "0000000391",
        listenerExt: "00000078A1"
    };
    var jsonText = JSON.stringify(bye);
    stompClient.send("/app/conferenceByUsername", {}, jsonText);
}

function playVoice() {
    var playVoice = {
        username: "0000000391",
        voiceCode: "00000",
        callId: callId
    };
    var jsonText = JSON.stringify(playVoice);
    stompClient.send("/app/playVoice", {}, jsonText);
}
function stopPlayVoice() {
    var stopPlayVoice = {
        username: "0000000391",
        voiceCode: "00000",
        callId: callId
    };
    var jsonText = JSON.stringify(stopPlayVoice);
    stompClient.send("/app/stopPlayVoice", {}, jsonText);
}

function transfer() {
    var transfer = {
        username: "0000000391",
        otherUsername: "0000000392",
        callId: callId
    };
    var jsonText = JSON.stringify(transfer);
    stompClient.send("/app/transfer", {}, jsonText);
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#bye" ).click(function() { bye(); });
    $( "#call" ).click(function() { call(); });
    $( "#idle" ).click(function() { idle(); });
    $( "#busy" ).click(function() { busy(); });

    $( "#eavesdrop" ).click(function() { eavesdrop(); });
    $( "#stopEavesdrop" ).click(function() { stopEavesdrop(); });
    $( "#conference" ).click(function() { conference(); });
    $( "#stopConference" ).click(function() { stopConference(); });
    $( "#conferenceByUsername" ).click(function() { conferenceByUsername(); });
    $( "#previewCall" ).click(function() { previewCall(); });
    $( "#stopPlayVoice" ).click(function() { stopPlayVoice(); });
    $( "#playVoice" ).click(function() { playVoice(); });
    $( "#transfer" ).click(function() { transfer(); });
});