/*
Autor: Florian Pronnegg, Jasmin Zach
Datum: 07.06.2022
Projektname: BikerBoost
 */

//Aufgaben der Klasse: Erstellung eines Meetings mit allen zugehörigen Funktionen

const { CallClient, VideoStreamRenderer, LocalVideoStream } = require('@azure/communication-calling');
const { AzureCommunicationTokenCredential } = require('@azure/communication-common');
const { AzureLogger, setLogLevel } = require("@azure/logger");

setLogLevel('verbose');
AzureLogger.log = (...args) => {
    console.log(...args);
};


let callAgent;
let deviceManager;
let call;
let incomingCall;
let localVideoStream;
let localVideoStreamRenderer;


let userAccessToken = document.getElementById('user-access-token');
let calleeAcsUserId = document.getElementById('callee-acs-user-id');
let initializeCallAgentButton = document.getElementById('initialize-call-agent');
let startCallButton = document.getElementById('start-call-button');
let hangUpCallButton = document.getElementById('hangup-call-button');
let acceptCallButton = document.getElementById('accept-call-button');
let startVideoButton = document.getElementById('start-video-button');
let stopVideoButton = document.getElementById('stop-video-button');
let connectedLabel = document.getElementById('connectedLabel');
let remoteVideoContainer = document.getElementById('remoteVideoContainer');
let localVideoContainer = document.getElementById('localVideoContainer');

initializeCallAgentButton.onclick = async () => {
    try {
        const callClient = new CallClient();
        tokenCredential = new AzureCommunicationTokenCredential(userAccessToken.value.trim());
        callAgent = await callClient.createCallAgent(tokenCredential)

        deviceManager = await callClient.getDeviceManager();
        await deviceManager.askDevicePermission({ video: true });
        await deviceManager.askDevicePermission({ audio: true });

        callAgent.on('incomingCall', async (args) => {
            try {
                incomingCall = args.incomingCall;
                acceptCallButton.disabled = false;
                startCallButton.disabled = true;
            } catch (error) {
                console.error(error);
            }
        });

        startCallButton.disabled = false;
        initializeCallAgentButton.disabled = true;
    } catch(error) {
        console.error(error);
    }
}

startCallButton.onclick = async () => {
    try {
        const localVideoStream = await createLocalVideoStream();
        const videoOptions = localVideoStream ? { localVideoStreams: [localVideoStream] } : undefined;
        call = callAgent.startCall([{ communicationUserId: calleeAcsUserId.value.trim() }], { videoOptions });

        subscribeToCall(call);
    } catch (error) {
        console.error(error);
    }
}


acceptCallButton.onclick = async () => {
    try {
        const localVideoStream = await createLocalVideoStream();
        const videoOptions = localVideoStream ? { localVideoStreams: [localVideoStream] } : undefined;
        call = await incomingCall.accept({ videoOptions });

        subscribeToCall(call);
    } catch (error) {
        console.error(error);
    }
}

subscribeToCall = (call) => {
    try {
        console.log(`Call Id: ${call.id}`);
        //Subscribe to call's 'idChanged' event for value changes.
        call.on('idChanged', () => {
            console.log(`Call Id changed: ${call.id}`);
        });


        console.log(`Call state: ${call.state}`);

        call.on('stateChanged', async () => {
            console.log(`Call state changed: ${call.state}`);
            if(call.state === 'Connected') {
                connectedLabel.hidden = false;
                acceptCallButton.disabled = true;
                startCallButton.disabled = true;
                hangUpCallButton.disabled = false;
                startVideoButton.disabled = false;
                stopVideoButton.disabled = false;
            } else if (call.state === 'Disconnected') {
                connectedLabel.hidden = true;
                startCallButton.disabled = false;
                hangUpCallButton.disabled = true;
                startVideoButton.disabled = true;
                stopVideoButton.disabled = true;
                console.log(`Call ended, call end reason={code=${call.callEndReason.code}, subCode=${call.callEndReason.subCode}}`);
            }
        });

        call.localVideoStreams.forEach(async (lvs) => {
            localVideoStream = lvs;
            await displayLocalVideoStream();
        });
        call.on('localVideoStreamsUpdated', e => {
            e.added.forEach(async (lvs) => {
                localVideoStream = lvs;
                await displayLocalVideoStream();
            });
            e.removed.forEach(lvs => {
                removeLocalVideoStream();
            });
        });

        call.remoteParticipants.forEach(remoteParticipant => {
            subscribeToRemoteParticipant(remoteParticipant);
        });

        call.on('remoteParticipantsUpdated', e => {

            e.added.forEach(remoteParticipant => {
                subscribeToRemoteParticipant(remoteParticipant)
            });

            e.removed.forEach(remoteParticipant => {
                console.log('Remote participant removed from the call.');
            });
        });
    } catch (error) {
        console.error(error);
    }
}

subscribeToRemoteParticipant = (remoteParticipant) => {
    try {

        console.log(`Remote participant state: ${remoteParticipant.state}`);

        remoteParticipant.on('stateChanged', () => {
            console.log(`Remote participant state changed: ${remoteParticipant.state}`);
        });


        remoteParticipant.videoStreams.forEach(remoteVideoStream => {
            subscribeToRemoteVideoStream(remoteVideoStream)
        });

        remoteParticipant.on('videoStreamsUpdated', e => {

            e.added.forEach(remoteVideoStream => {
                subscribeToRemoteVideoStream(remoteVideoStream)
            });

            e.removed.forEach(remoteVideoStream => {
                console.log('Remote participant video stream was removed.');
            })
        });
    } catch (error) {
        console.error(error);
    }
}

subscribeToRemoteVideoStream = async (remoteVideoStream) => {

    let videoStreamRenderer = new VideoStreamRenderer(remoteVideoStream);
    let view;
    const renderVideo = async () => {
        try {

            view = await videoStreamRenderer.createView();

            remoteVideoContainer.hidden = false;
            remoteVideoContainer.appendChild(view.target);
        } catch (e) {
            console.warn(`Failed to createView, reason=${e.message}, code=${e.code}`);
        }
    }

    remoteVideoStream.on('isAvailableChanged', async () => {

        if (remoteVideoStream.isAvailable) {
            await renderVideo();

        } else {
            if (view) {
                view.dispose();
                view = undefined;
            }
        }
    });

    if (remoteVideoStream.isAvailable) {
        await renderVideo();
    }
}

startVideoButton.onclick = async () => {
    try {
        const localVideoStream = await createLocalVideoStream();
        await call.startVideo(localVideoStream);
    } catch (error) {
        console.error(error);
    }
}

stopVideoButton.onclick = async () => {
    try {
        await call.stopVideo(localVideoStream);
    } catch (error) {
        console.error(error);
    }
}


createLocalVideoStream = async () => {
    const camera = (await deviceManager.getCameras())[0];
    if (camera) {
        return new LocalVideoStream(camera);
    } else {
        console.error(`No camera device found on the system`);
    }
}
displayLocalVideoStream = async () => {
    try {
        localVideoStreamRenderer = new VideoStreamRenderer(localVideoStream);
        const view = await localVideoStreamRenderer.createView();
        localVideoContainer.hidden = false;
        localVideoContainer.appendChild(view.target);
    } catch (error) {
        console.error(error);
    }
}
removeLocalVideoStream = async() => {
    try {
        localVideoStreamRenderer.dispose();
        localVideoContainer.hidden = true;
    } catch (error) {
        console.error(error);
    }
}

hangUpCallButton.addEventListener("click", async () => {
    await call.hangUp();
});