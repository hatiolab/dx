package com.hatiolab.dx.packet;

public class Code {
	
    /* Discovery */

    public final static int DX_DISCOVERY_BASE               = 0x00;
    public final static int DX_DISCOVERY_REQUEST            = (DX_DISCOVERY_BASE + 0);
    public final static int DX_DISCOVERY_RESPONSE           = (DX_DISCOVERY_BASE + 1);

    /* Persistent Settings */

    public final static int DX_SETTING_BASE                 = 0x00;

    /* Volatile Status - 상태 */

    public final static int DX_STAT_BASE                    = 0x00;

    /* Event Types */

    public final static int DX_EVT_BASE                     = 0x00;
    public final static int DX_EVT_CONNECT                  = (DX_EVT_BASE + 0);  /* 옴니드라이브가 연결되었을 때 발생 */
    public final static int DX_EVT_DISCONNECT               = (DX_EVT_BASE + 1);  /* 옴니드라이브 연결이 끊겼을 때 발생 */
    public final static int DX_EVT_ERROR                    = (DX_EVT_BASE + 2);  /* 오류 발생 이벤트 */
    public final static int DX_EVT_ALARM                    = (DX_EVT_BASE + 3);  /* 경고 발생 이벤트 */

    /* Alarm Code */

    public final static int DX_ALM_BASE                     = 0x00;

    /* Error Code */

    public final static int DX_ERR_BASE                     = 0x00;

    /* Commands */

    public final static int DX_CMD_BASE                         = 0x00;

    /* File */

    public final static int DX_FILE_BASE                    = 0x00;
    public final static int DX_FILE_GET_LIST                = (DX_FILE_BASE + 0);	/* 파일리스트 요청 */
    public final static int DX_FILE_LIST                	= (DX_FILE_BASE + 1);	/* 파일리스트 정보 */
    public final static int DX_FILE_GET                		= (DX_FILE_BASE + 2);	/* 부분 파일 내용 요청 */
    public final static int DX_FILE                			= (DX_FILE_BASE + 3);	/* 부분 파일 내용 */
    public final static int DX_FILE_DELETE          		= (DX_FILE_BASE + 4);	/* 파일 삭제 요청 */
    
    /* Stream */
    public final static int DX_STREAM_BASE					= 0x00;
    public final static int DX_STREAM						= (DX_STREAM_BASE + 0);	/* Stream 데이타 */

}
