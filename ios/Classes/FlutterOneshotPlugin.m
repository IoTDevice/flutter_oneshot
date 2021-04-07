#import "FlutterOneshotPlugin.h"
#if __has_include(<flutter_oneshot/flutter_oneshot-Swift.h>)
#import <flutter_oneshot/flutter_oneshot-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_oneshot-Swift.h"
#endif

@implementation FlutterOneshotPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterOneshotPlugin registerWithRegistrar:registrar];
}
@end
