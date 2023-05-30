/**
 * @providesModule JitsiMeet
 */

import { NativeModules, requireNativeComponent } from 'react-native';

export const JitsiMeetModule = NativeModules.JitsiMeetModule
const call = JitsiMeetModule.call;
const endCall = JitsiMeetModule.endCall;
JitsiMeetModule.call = (url, userInfo, meetOptions, meetFeatureFlags) => {
  userInfo = userInfo || {};
  meetOptions = meetOptions || {};
  meetFeatureFlags = meetFeatureFlags || {};
  call(url, userInfo, meetOptions, meetFeatureFlags);
}
JitsiMeetModule.endCall = () => {
  endCall();
}
export default JitsiMeetModule;