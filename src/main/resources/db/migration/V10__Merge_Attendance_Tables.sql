-- 将attendance_record表中的数据迁移到pe_course_attendance表
INSERT INTO pe_course_attendance (
    course_id,
    student_id,
    attendance_date,
    status,
    remark,
    create_time,
    update_time,
    deleted
)
SELECT 
    course_id,
    student_id,
    attendance_date,
    status,
    remark,
    create_time,
    update_time,
    is_deleted
FROM attendance_record
WHERE is_deleted = 0
ON DUPLICATE KEY UPDATE
    status = VALUES(status),
    remark = VALUES(remark),
    update_time = VALUES(update_time);

-- 删除旧的attendance_record表
DROP TABLE IF EXISTS attendance_record;

-- 修改pe_course_attendance表，添加考勤时间字段
ALTER TABLE pe_course_attendance
ADD COLUMN attendance_time TIME AFTER attendance_date; 