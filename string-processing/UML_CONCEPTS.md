# 字串處理與基礎API - UML概念圖

## 📊 模組概念關係圖

```mermaid
graph TB
    subgraph "String 核心概念"
        A[String類別] --> B[不可變性]
        A --> C[字串常量池]
        A --> D[UTF-16編碼]
        B --> E[記憶體管理]
        C --> F[效能優化]
    end
    
    subgraph "字串操作"
        G[字串比較] --> H[equals/equalsIgnoreCase]
        G --> I[compareTo]
        
        J[字串搜尋] --> K[indexOf/lastIndexOf]
        J --> L[contains/startsWith/endsWith]
        
        M[字串修改] --> N[substring/split]
        M --> O[replace/replaceAll]
        M --> P[trim/strip]
        M --> Q[toUpperCase/toLowerCase]
    end
    
    subgraph "可變字串"
        R[StringBuilder] --> S[append/insert]
        R --> T[delete/replace]
        R --> U[capacity管理]
        
        V[StringBuffer] --> W[執行緒安全]
        V --> X[同步開銷]
    end
    
    subgraph "輸入處理"
        Y[Scanner] --> Z[基本型別輸入]
        Y --> AA[字串輸入]
        Y --> BB[文件讀取]
        Y --> CC[輸入驗證]
        Y --> DD[分隔符設定]
    end
    
    subgraph "格式化輸出"
        EE[printf] --> FF[格式化指定符]
        EE --> GG[寬度對齊]
        
        HH[String.format] --> II[字串模板]
        HH --> JJ[參數替換]
        
        KK[DecimalFormat] --> LL[數字格式化]
        KK --> MM[貨幣符號]
    end
    
    subgraph "正則表達式"
        NN[Pattern] --> OO[編譯模式]
        NN --> PP[群組捕獲]
        
        QQ[Matcher] --> RR[匹配操作]
        QQ --> SS[替換操作]
        
        TT[String正則方法] --> UU[matches/replaceAll]
        TT --> VV[split]
    end
    
    style A fill:#ff9999
    style R fill:#99ccff
    style Y fill:#99ff99
    style EE fill:#ffcc99
    style NN fill:#cc99ff
```

## 🔄 字串處理工作流程

```mermaid
flowchart TD
    Start([字串輸入]) --> InputType{輸入類型}
    
    InputType -->|控制台輸入| Scanner1[Scanner處理]
    InputType -->|文件輸入| Scanner2[文件Scanner]
    InputType -->|字串字面量| Direct[直接使用]
    
    Scanner1 --> Validate[輸入驗證]
    Scanner2 --> Validate
    Direct --> Process[字串處理]
    
    Validate --> Valid{驗證通過?}
    Valid -->|否| Error[錯誤處理]
    Valid -->|是| Process
    Error --> Scanner1
    
    Process --> Operation{操作類型}
    
    Operation -->|查詢| Search[搜尋操作]
    Operation -->|修改| Modify[修改操作]
    Operation -->|格式化| Format[格式化操作]
    Operation -->|驗證| Regex[正則表達式]
    
    Search --> SearchMethod[indexOf/contains/matches]
    Modify --> ModifyType{修改量}
    
    ModifyType -->|少量| StringOp[String操作]
    ModifyType -->|大量| StringBuilder1[StringBuilder]
    
    StringOp --> Result[處理結果]
    StringBuilder1 --> Result
    SearchMethod --> Result
    
    Format --> FormatType{格式類型}
    FormatType -->|簡單| Printf[printf]
    FormatType -->|複雜| StringFormat[String.format]
    FormatType -->|數字| DecimalFormat1[DecimalFormat]
    
    Printf --> Output[格式化輸出]
    StringFormat --> Output
    DecimalFormat1 --> Output
    
    Regex --> RegexOp[Pattern/Matcher]
    RegexOp --> RegexResult[正則結果]
    
    Result --> End([處理完成])
    Output --> End
    RegexResult --> End
    
    style Start fill:#e1f5fe
    style End fill:#c8e6c9
    style Valid fill:#fff3e0
    style ModifyType fill:#fff3e0
    style FormatType fill:#fff3e0
```

## 🎯 效能考量與選擇決策

```mermaid
graph TD
    Decision[字串操作決策] --> Question1{操作頻率}
    
    Question1 -->|單次操作| String1[使用String]
    Question1 -->|多次操作| Question2{執行緒安全需求}
    
    Question2 -->|需要| StringBuffer1[使用StringBuffer]
    Question2 -->|不需要| StringBuilder2[使用StringBuilder]
    
    String1 --> Concern1{效能考量}
    StringBuilder2 --> Concern2{容量設定}
    StringBuffer1 --> Concern3{同步開銷}
    
    Concern1 -->|高效能需求| Reconsider[重新考慮StringBuilder]
    Concern1 -->|一般需求| StringOK[String可接受]
    
    Concern2 -->|已知大小| PreAlloc[預分配容量]
    Concern2 -->|未知大小| DefaultCap[使用默認容量]
    
    Concern3 -->|效能敏感| ConsiderString[考慮其他方案]
    Concern3 -->|安全優先| BufferOK[StringBuffer可接受]
    
    subgraph "記憶體使用模式"
        Memory1[String: 不可變<br/>每次操作創建新物件]
        Memory2[StringBuilder: 可變緩衝區<br/>動態擴容]
        Memory3[StringBuffer: 可變緩衝區<br/>執行緒安全]
    end
    
    String1 -.-> Memory1
    StringBuilder2 -.-> Memory2
    StringBuffer1 -.-> Memory3
    
    style Decision fill:#ff9999
    style Question1 fill:#fff3e0
    style Question2 fill:#fff3e0
    style Concern1 fill:#ffeb3b
    style Concern2 fill:#ffeb3b
    style Concern3 fill:#ffeb3b
```

## 📈 學習進度與技能層次

```mermaid
pyramid TB
    subgraph "專家級 (Expert)"
        Level5[正則表達式高級應用<br/>效能調優與記憶體管理<br/>文本處理框架設計]
    end
    
    subgraph "高級 (Advanced)"
        Level4[複雜正則表達式<br/>文件處理與解析<br/>國際化處理]
    end
    
    subgraph "中級 (Intermediate)"
        Level3[StringBuilder最佳化<br/>格式化輸出控制<br/>基礎正則表達式]
    end
    
    subgraph "初級 (Basic)"
        Level2[String基本操作<br/>Scanner輸入處理<br/>簡單格式化]
    end
    
    subgraph "入門 (Foundation)"
        Level1[String創建與比較<br/>基本字串方法<br/>控制台輸出]
    end
    
    Level1 --> Level2
    Level2 --> Level3
    Level3 --> Level4
    Level4 --> Level5
    
    style Level1 fill:#c8e6c9
    style Level2 fill:#81c784
    style Level3 fill:#4caf50
    style Level4 fill:#2e7d32
    style Level5 fill:#1b5e20
```

## 🛠️ 實際應用場景流程

```mermaid
graph LR
    subgraph "數據驗證"
        A1[用戶輸入] --> A2[格式驗證]
        A2 --> A3[正則匹配]
        A3 --> A4[錯誤提示]
    end
    
    subgraph "文件處理"
        B1[文件讀取] --> B2[內容解析]
        B2 --> B3[資料提取]
        B3 --> B4[格式轉換]
    end
    
    subgraph "報表生成"
        C1[資料收集] --> C2[格式化處理]
        C2 --> C3[模板套用]
        C3 --> C4[輸出生成]
    end
    
    subgraph "日誌處理"
        D1[日誌收集] --> D2[模式匹配]
        D2 --> D3[資訊提取]
        D3 --> D4[統計分析]
    end
    
    style A1 fill:#ffcdd2
    style B1 fill:#f8bbd9
    style C1 fill:#e1bee7
    style D1 fill:#d1c4e9
```

## 🔗 與其他模組的整合關係

```mermaid
graph TD
    StringProcessing[M02: 字串處理與基礎API]
    
    %% 前置依賴
    Methods[M01: 方法定義與呼叫] --> StringProcessing
    
    %% 輸出到其他模組
    StringProcessing --> Collections[M03: 陣列與集合<br/>字串集合處理]
    StringProcessing --> OOP[M04: 物件導向<br/>toString/equals實作]
    StringProcessing --> Algorithms[M05: 演算法<br/>字串演算法]
    StringProcessing --> Generics[M06: 泛型<br/>泛型字串處理]
    
    %% 實際應用領域
    StringProcessing --> WebDev[Web開發<br/>HTTP請求處理]
    StringProcessing --> DataProcessing[資料處理<br/>CSV/JSON解析]
    StringProcessing --> FileIO[文件操作<br/>配置文件處理]
    StringProcessing --> Validation[資料驗證<br/>表單驗證]
    
    %% 技術整合
    StringProcessing --> Database[資料庫<br/>SQL字串處理]
    StringProcessing --> Network[網路程式設計<br/>協議解析]
    StringProcessing --> Security[安全<br/>輸入清理]
    
    style StringProcessing fill:#ff9999
    style Methods fill:#e1f5fe
    style Collections fill:#99ff99
    style OOP fill:#ffcc99
    style WebDev fill:#81c784
    style DataProcessing fill:#64b5f6
    style Database fill:#ba68c8
```