/**
 * @providesModule JitsiMeet
 */

import { NativeModules, requireNativeComponent} from 'react-native';
const JitsiMeetModule = NativeModules.JitsiMeetModule
const ExJitsiMeetView = requireNativeComponent('ExJitsiMeetView');
const call = JitsiMeetModule.call;
const endCall = JitsiMeetModule.endCall;
JitsiMeetModule.call = (domain, roomName, userInfo, meetOptions, meetFeatureFlags, eventEmitter) => {
  userInfo = userInfo || {};
  meetOptions = meetOptions || {};
  meetFeatureFlags = meetFeatureFlags || {};
  const callbackJitsi = (a, b) => {
    console.log("callbackJitsi::", { a, b })
    eventEmitter.emit(b)
  }
  call(domain, roomName, userInfo, meetOptions, meetFeatureFlags, callbackJitsi);
}
JitsiMeetModule.endCall = () => {
  endCall();
}

const WrapperExHeaderMeet = (props) => {
  return <ExJitsiMeetView {...props} />
}
JitsiMeetModule.ExJitsiMeetView = WrapperExHeaderMeet;

export default JitsiMeetModule;


