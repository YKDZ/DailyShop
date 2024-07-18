# DailyShop

English | [简体中文](README_ZH_CN.md)

# Intro

A SpigotMC plugin inspired by [DailyShop](https://github.com/divios/DailyShop). Pursuing ultra customizability and efficiency.

## Document

You can find the document [here](https://docs.encmys.cn/s/ykdz-plugin-docs).

## Todo

- [x] Dynamic pricing based on market feedback
- [ ] BE GUI
- [ ] Total market volume
- [x] Transaction log
- [x] SQL Support
- [ ] Random amount and amount based price
- [ ] List product by condition
- [ ] Discount
- [ ] Manually specifying restock results
- [ ] Transition limit
- [ ] Cart
- [ ] Merchant

## API

### Maven

```
<repositories>
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io/</url>
    </repository>
</repositories>
```

```
<dependencies>
    <dependency>
        <groupId>cn.encmys</groupId>
        <artifactId>DailyShop</artifactId>
        <version>{VERSION}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Gradle (Kotlin)

```kotlin
repositories {
    maven("https://jitpack.io/")
}
```

```kotlin
dependencies {
    compileOnly("cn.encmys:DailyShop:{VERSION}")
}
```

## Thanks to

- [InvUI](https://github.com/NichtStudioCode/InvUI)
- [CommandAPI](https://github.com/JorelAli/CommandAPI)
- [ItemsLangAPI](https://github.com/Rubix327/ItemsLangAPI)

that make this plugin possible.