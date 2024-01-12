#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class Kotlin_mpp_godtools_tool_parserNapierLogLevel, Kotlin_mpp_godtools_tool_parserKotlinThrowable, Kotlin_mpp_godtools_tool_parserKotlinEnumCompanion, Kotlin_mpp_godtools_tool_parserKotlinEnum<E>, Kotlin_mpp_godtools_tool_parserKotlinArray<T>;

@protocol Kotlin_mpp_godtools_tool_parserKotlinComparable, Kotlin_mpp_godtools_tool_parserKotlinIterator;

NS_ASSUME_NONNULL_BEGIN
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunknown-warning-option"
#pragma clang diagnostic ignored "-Wincompatible-property-type"
#pragma clang diagnostic ignored "-Wnullability"

#pragma push_macro("_Nullable_result")
#if !__has_feature(nullability_nullable_result)
#undef _Nullable_result
#define _Nullable_result _Nullable
#endif

__attribute__((swift_name("KotlinBase")))
@interface Kotlin_mpp_godtools_tool_parserBase : NSObject
- (instancetype)init __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
+ (void)initialize __attribute__((objc_requires_super));
@end

@interface Kotlin_mpp_godtools_tool_parserBase (Kotlin_mpp_godtools_tool_parserBaseCopying) <NSCopying>
@end

__attribute__((swift_name("KotlinMutableSet")))
@interface Kotlin_mpp_godtools_tool_parserMutableSet<ObjectType> : NSMutableSet<ObjectType>
@end

__attribute__((swift_name("KotlinMutableDictionary")))
@interface Kotlin_mpp_godtools_tool_parserMutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end

@interface NSError (NSErrorKotlin_mpp_godtools_tool_parserKotlinException)
@property (readonly) id _Nullable kotlinException;
@end

__attribute__((swift_name("KotlinNumber")))
@interface Kotlin_mpp_godtools_tool_parserNumber : NSNumber
- (instancetype)initWithChar:(char)value __attribute__((unavailable));
- (instancetype)initWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
- (instancetype)initWithShort:(short)value __attribute__((unavailable));
- (instancetype)initWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
- (instancetype)initWithInt:(int)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
- (instancetype)initWithLong:(long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
- (instancetype)initWithLongLong:(long long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
- (instancetype)initWithFloat:(float)value __attribute__((unavailable));
- (instancetype)initWithDouble:(double)value __attribute__((unavailable));
- (instancetype)initWithBool:(BOOL)value __attribute__((unavailable));
- (instancetype)initWithInteger:(NSInteger)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
+ (instancetype)numberWithChar:(char)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
+ (instancetype)numberWithShort:(short)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
+ (instancetype)numberWithInt:(int)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
+ (instancetype)numberWithLong:(long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
+ (instancetype)numberWithLongLong:(long long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
+ (instancetype)numberWithFloat:(float)value __attribute__((unavailable));
+ (instancetype)numberWithDouble:(double)value __attribute__((unavailable));
+ (instancetype)numberWithBool:(BOOL)value __attribute__((unavailable));
+ (instancetype)numberWithInteger:(NSInteger)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
@end

__attribute__((swift_name("KotlinByte")))
@interface Kotlin_mpp_godtools_tool_parserByte : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithChar:(char)value;
+ (instancetype)numberWithChar:(char)value;
@end

__attribute__((swift_name("KotlinUByte")))
@interface Kotlin_mpp_godtools_tool_parserUByte : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithUnsignedChar:(unsigned char)value;
+ (instancetype)numberWithUnsignedChar:(unsigned char)value;
@end

__attribute__((swift_name("KotlinShort")))
@interface Kotlin_mpp_godtools_tool_parserShort : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithShort:(short)value;
+ (instancetype)numberWithShort:(short)value;
@end

__attribute__((swift_name("KotlinUShort")))
@interface Kotlin_mpp_godtools_tool_parserUShort : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithUnsignedShort:(unsigned short)value;
+ (instancetype)numberWithUnsignedShort:(unsigned short)value;
@end

__attribute__((swift_name("KotlinInt")))
@interface Kotlin_mpp_godtools_tool_parserInt : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithInt:(int)value;
+ (instancetype)numberWithInt:(int)value;
@end

__attribute__((swift_name("KotlinUInt")))
@interface Kotlin_mpp_godtools_tool_parserUInt : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithUnsignedInt:(unsigned int)value;
+ (instancetype)numberWithUnsignedInt:(unsigned int)value;
@end

__attribute__((swift_name("KotlinLong")))
@interface Kotlin_mpp_godtools_tool_parserLong : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithLongLong:(long long)value;
+ (instancetype)numberWithLongLong:(long long)value;
@end

__attribute__((swift_name("KotlinULong")))
@interface Kotlin_mpp_godtools_tool_parserULong : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value;
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value;
@end

__attribute__((swift_name("KotlinFloat")))
@interface Kotlin_mpp_godtools_tool_parserFloat : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithFloat:(float)value;
+ (instancetype)numberWithFloat:(float)value;
@end

__attribute__((swift_name("KotlinDouble")))
@interface Kotlin_mpp_godtools_tool_parserDouble : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithDouble:(double)value;
+ (instancetype)numberWithDouble:(double)value;
@end

__attribute__((swift_name("KotlinBoolean")))
@interface Kotlin_mpp_godtools_tool_parserBoolean : Kotlin_mpp_godtools_tool_parserNumber
- (instancetype)initWithBool:(BOOL)value;
+ (instancetype)numberWithBool:(BOOL)value;
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NapierProxyKt")))
@interface Kotlin_mpp_godtools_tool_parserNapierProxyKt : Kotlin_mpp_godtools_tool_parserBase
+ (void)enableCustomLoggingOnLog:(void (^)(Kotlin_mpp_godtools_tool_parserNapierLogLevel *, NSString * _Nullable, Kotlin_mpp_godtools_tool_parserKotlinThrowable * _Nullable, NSString * _Nullable))onLog __attribute__((swift_name("enableCustomLogging(onLog:)")));
+ (void)enableDebugLogging __attribute__((swift_name("enableDebugLogging()")));
@end

__attribute__((swift_name("KotlinComparable")))
@protocol Kotlin_mpp_godtools_tool_parserKotlinComparable
@required
- (int32_t)compareToOther:(id _Nullable)other __attribute__((swift_name("compareTo(other:)")));
@end

__attribute__((swift_name("KotlinEnum")))
@interface Kotlin_mpp_godtools_tool_parserKotlinEnum<E> : Kotlin_mpp_godtools_tool_parserBase <Kotlin_mpp_godtools_tool_parserKotlinComparable>
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) Kotlin_mpp_godtools_tool_parserKotlinEnumCompanion *companion __attribute__((swift_name("companion")));
- (int32_t)compareToOther:(E)other __attribute__((swift_name("compareTo(other:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) int32_t ordinal __attribute__((swift_name("ordinal")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NapierLogLevel")))
@interface Kotlin_mpp_godtools_tool_parserNapierLogLevel : Kotlin_mpp_godtools_tool_parserKotlinEnum<Kotlin_mpp_godtools_tool_parserNapierLogLevel *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) Kotlin_mpp_godtools_tool_parserNapierLogLevel *verbose __attribute__((swift_name("verbose")));
@property (class, readonly) Kotlin_mpp_godtools_tool_parserNapierLogLevel *debug __attribute__((swift_name("debug")));
@property (class, readonly) Kotlin_mpp_godtools_tool_parserNapierLogLevel *info __attribute__((swift_name("info")));
@property (class, readonly) Kotlin_mpp_godtools_tool_parserNapierLogLevel *warning __attribute__((swift_name("warning")));
@property (class, readonly) Kotlin_mpp_godtools_tool_parserNapierLogLevel *error __attribute__((swift_name("error")));
@property (class, readonly) Kotlin_mpp_godtools_tool_parserNapierLogLevel *assert __attribute__((swift_name("assert")));
+ (Kotlin_mpp_godtools_tool_parserKotlinArray<Kotlin_mpp_godtools_tool_parserNapierLogLevel *> *)values __attribute__((swift_name("values()")));
@end

__attribute__((swift_name("KotlinThrowable")))
@interface Kotlin_mpp_godtools_tool_parserKotlinThrowable : Kotlin_mpp_godtools_tool_parserBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(Kotlin_mpp_godtools_tool_parserKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(Kotlin_mpp_godtools_tool_parserKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));

/**
 * @note annotations
 *   kotlin.experimental.ExperimentalNativeApi
*/
- (Kotlin_mpp_godtools_tool_parserKotlinArray<NSString *> *)getStackTrace __attribute__((swift_name("getStackTrace()")));
- (void)printStackTrace __attribute__((swift_name("printStackTrace()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) Kotlin_mpp_godtools_tool_parserKotlinThrowable * _Nullable cause __attribute__((swift_name("cause")));
@property (readonly) NSString * _Nullable message __attribute__((swift_name("message")));
- (NSError *)asError __attribute__((swift_name("asError()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinEnumCompanion")))
@interface Kotlin_mpp_godtools_tool_parserKotlinEnumCompanion : Kotlin_mpp_godtools_tool_parserBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) Kotlin_mpp_godtools_tool_parserKotlinEnumCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinArray")))
@interface Kotlin_mpp_godtools_tool_parserKotlinArray<T> : Kotlin_mpp_godtools_tool_parserBase
+ (instancetype)arrayWithSize:(int32_t)size init:(T _Nullable (^)(Kotlin_mpp_godtools_tool_parserInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (T _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (id<Kotlin_mpp_godtools_tool_parserKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(T _Nullable)value __attribute__((swift_name("set(index:value:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((swift_name("KotlinIterator")))
@protocol Kotlin_mpp_godtools_tool_parserKotlinIterator
@required
- (BOOL)hasNext __attribute__((swift_name("hasNext()")));
- (id _Nullable)next __attribute__((swift_name("next()")));
@end

#pragma pop_macro("_Nullable_result")
#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END
